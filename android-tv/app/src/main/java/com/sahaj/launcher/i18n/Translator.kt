package com.sahaj.launcher.i18n

import android.widget.TextView

/** Language codes used everywhere in the app. */
object Lang {
    const val HI = "hi"   // Hindi (primary / default)
    const val EN = "en"   // English
    val all = listOf(HI, EN)
}

/**
 * Looks up the text for a key in the CURRENT language.
 * The wording itself lives in TextMap.kt (not here).
 */
object Translator {
    /** Language currently shown on screen. Changed from the Settings pop-up. */
    var language: String = Lang.HI

    /**
     * Returns the text for [key]. If a language line is missing it falls back to
     * Hindi, then English, then the key name itself (so you can spot the gap).
     * [args] fill in %s placeholders.
     */
    fun t(key: String, vararg args: Any): String {
        val entry = TextMap.entries[key]
        val raw = entry?.get(language) ?: entry?.get(Lang.HI) ?: entry?.get(Lang.EN) ?: key
        return if (args.isEmpty()) raw else String.format(raw, *args)
    }
}

/**
 * Keeps a list of "this view shows that text" rules, so that when the language
 * changes we can refresh every text on a screen with one call: refresh().
 */
class TextBinder {
    private val updaters = ArrayList<() -> Unit>()

    /** Show the text for [key] in [view] (and keep it updated on refresh). */
    fun bind(view: TextView, key: String) {
        add { view.text = Translator.t(key) }
    }

    /** Show whatever [provider] returns (used for clock, date, "Current theme: %s", ...). */
    fun bind(view: TextView, provider: () -> String) {
        add { view.text = provider() }
    }

    /** Re-apply every rule - call after the language (or other data) changed. */
    fun refresh() {
        for (updater in updaters) updater()
    }

    private fun add(updater: () -> Unit) {
        updaters.add(updater)
        updater()
    }
}
