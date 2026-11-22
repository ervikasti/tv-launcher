package com.sahaj.launcher.config

import android.graphics.Color
import android.graphics.drawable.Drawable
import com.sahaj.launcher.R
import com.sahaj.launcher.i18n.Keys
import com.sahaj.launcher.i18n.Translator

/**
 * How the logo inside a tile is drawn.
 *  - drawableRes : a picture from res/drawable (optional)
 *  - drawable    : a ready picture, e.g. the app's own icon from the TV (optional)
 *  - wordmark    : plain text drawn as logo, e.g. "Sony LIV" (optional)
 */
data class Logo(
    val drawableRes: Int? = null,
    val drawable: Drawable? = null,
    val wordmark: String? = null,
    val wordmarkColor: Int = Color.BLACK,
    val wordmarkSizeSp: Float = 16f,
    val wordmarkFont: String = "sans-serif-medium",
)

/**
 * One app tile on the home screen.
 *  - nameKey / descKey : text keys (wording is in TextMap.kt)
 *  - packages          : Android package names to try, in order. The first one
 *                        installed on the TV is opened.
 *  - tileColor/inkColor: tile background and logo/label colour
 *  - label             : name used as-is (only for apps the viewer picked in Settings;
 *                        built-in tiles leave it empty and use nameKey instead)
 */
data class AppEntry(
    val id: String,
    val nameKey: String,
    val descKey: String,
    val packages: List<String>,
    val tileColor: Int,
    val inkColor: Int,
    val logo: Logo,
    val label: String? = null,
)

/** The name to show for this app, in the current language. */
fun AppEntry.displayName(): String = label ?: Translator.t(nameKey)

/**
 * THE DEFAULT LIST OF TILES ON THE HOME SCREEN (left to right).
 * The viewer can change which apps are shown from Settings -> "Choose apps";
 * this list is what is shown until they do, and what "Restore default apps" brings back.
 *
 * To reorder: move a block up/down.   To remove: delete a block.
 * To fix an app that does not open: correct its package name in `packages`.
 *   (Find a package name on your TV with:  adb shell pm list packages | grep sonyliv)
 * Package names below are the usual Android TV ones - please verify on your TV.
 */
object AppCatalog {
    val apps: List<AppEntry> = listOf(
        AppEntry(
            id = "youtube",
            nameKey = Keys.APP_YOUTUBE_NAME,
            descKey = Keys.APP_YOUTUBE_DESC,
            packages = listOf("com.google.android.youtube.tv"),
            tileColor = Color.parseColor("#faeeeb"),
            inkColor = Color.parseColor("#b4372b"),
            logo = Logo(drawableRes = R.drawable.logo_youtube),
        ),
        AppEntry(
            id = "sony",
            nameKey = Keys.APP_SONY_NAME,
            descKey = Keys.APP_SONY_DESC,
            packages = listOf("com.sonyliv", "com.sony.liv", "com.sonyliv.tv", "com.sony.dtv.sonyliv"),
            tileColor = Color.parseColor("#edf2fa"),
            inkColor = Color.parseColor("#1e3a8a"),
            logo = Logo(
                wordmark = "Sony LIV",
                wordmarkColor = Color.parseColor("#1e293b"),
                wordmarkSizeSp = 24f,
                wordmarkFont = "sans-serif-black",
            ),
        ),
        AppEntry(
            id = "jiohotstar",
            nameKey = Keys.APP_JIOHOTSTAR_NAME,
            descKey = Keys.APP_JIOHOTSTAR_DESC,
            packages = listOf("in.startv.hotstaronly", "in.startv.hotstartvonly", "in.startv.hotstar"),
            tileColor = Color.parseColor("#e7e7f2"),
            inkColor = Color.parseColor("#474a7b"),
            logo = Logo(
                drawableRes = R.drawable.logo_star,
                wordmark = "JioHotstar",
                wordmarkColor = Color.parseColor("#424774"),
                wordmarkSizeSp = 15f,
                wordmarkFont = "sans-serif-medium",
            ),
        ),
    )
}
