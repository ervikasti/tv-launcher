package com.sahaj.launcher.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.sahaj.launcher.config.AppCatalog
import com.sahaj.launcher.config.AppEntry
import com.sahaj.launcher.config.Logo
import com.sahaj.launcher.i18n.Keys

/** One line in the "Choose apps" list of Settings. */
class Choice(
    val key: String,        // what is saved (see AppChoices)
    val entry: AppEntry?,   // set for the built-in tiles (YouTube, Sony...)
    val label: String?,     // set for any other app installed on the TV
)

/**
 * Works out WHICH apps appear on the home row.
 *
 * Every app has a "key" that is saved in Prefs:
 *   - built-in tile  -> its id from AppCatalog, e.g. "sony"
 *   - any other app  -> "pkg:" + its package name, e.g. "pkg:com.spotify.tv.android"
 */
object AppChoices {
    const val CUSTOM_PREFIX = "pkg:"

    /** The keys of the default tiles (AppCatalog.kt). */
    fun defaultKeys(): List<String> = AppCatalog.apps.map { it.id }

    /** The tiles to show right now: the viewer's choice, or the default list. */
    fun currentEntries(context: Context, prefs: Prefs): List<AppEntry> {
        val entries = resolve(context, prefs.appKeys ?: defaultKeys())
        return if (entries.isEmpty()) AppCatalog.apps else entries
    }

    /** Turns saved keys into tiles. Apps that were uninstalled since are skipped. */
    fun resolve(context: Context, keys: List<String>): List<AppEntry> = keys.mapNotNull { key ->
        if (key.startsWith(CUSTOM_PREFIX)) {
            customEntry(context, key.removePrefix(CUSTOM_PREFIX))
        } else {
            AppCatalog.apps.firstOrNull { it.id == key }
        }
    }

    /**
     * Everything the viewer can pick: the built-in tiles first, then all other apps
     * installed on the TV (alphabetical). Our own launcher is left out.
     */
    fun listChoices(context: Context): List<Choice> {
        val builtIn = AppCatalog.apps.map { Choice(it.id, it, null) }
        val covered = AppCatalog.apps.flatMap { it.packages }.toSet()
        val pm = context.packageManager

        // Collect launchable apps (TV apps and normal apps), one per package.
        val found = LinkedHashMap<String, String>()   // package -> name
        for (category in listOf(Intent.CATEGORY_LEANBACK_LAUNCHER, Intent.CATEGORY_LAUNCHER)) {
            val intent = Intent(Intent.ACTION_MAIN).addCategory(category)
            for (info in pm.queryIntentActivities(intent, 0)) {
                val pkg = info.activityInfo.packageName
                if (pkg == context.packageName || pkg in covered || found.containsKey(pkg)) continue
                found[pkg] = info.loadLabel(pm).toString()
            }
        }
        val others = found.entries
            .sortedBy { it.value.lowercase() }
            .map { Choice(CUSTOM_PREFIX + it.key, null, it.value) }
        return builtIn + others
    }

    /** Builds a tile for an app the viewer picked; null if it is no longer installed. */
    private fun customEntry(context: Context, pkg: String): AppEntry? {
        val pm = context.packageManager
        if (pm.getLeanbackLaunchIntentForPackage(pkg) == null && pm.getLaunchIntentForPackage(pkg) == null) {
            return null
        }
        val info = try {
            pm.getApplicationInfo(pkg, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            return null
        }
        // Reuse the soft tile colours of the built-in tiles (same app always gets the same colour).
        val colorIndex = Math.abs(pkg.hashCode()) % AppCatalog.apps.size
        val look = AppCatalog.apps[colorIndex]
        return AppEntry(
            id = CUSTOM_PREFIX + pkg,
            nameKey = "",
            descKey = Keys.APP_GENERIC_DESC,
            packages = listOf(pkg),
            tileColor = look.tileColor,
            inkColor = look.inkColor,
            logo = Logo(drawable = pm.getApplicationIcon(info)),
            label = pm.getApplicationLabel(info).toString(),
        )
    }
}
