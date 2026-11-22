package com.sahaj.launcher.data

import android.content.Context
import com.sahaj.launcher.i18n.Lang

/**
 * Saves the viewer's choices on the TV (language, colour theme, large text),
 * so they are remembered after restart.
 */
class Prefs(context: Context) {
    private val store = context.getSharedPreferences("sahaj_prefs", Context.MODE_PRIVATE)

    /** "hi" or "en". Hindi by default. */
    var language: String
        get() = store.getString(KEY_LANGUAGE, Lang.HI) ?: Lang.HI
        set(value) = store.edit().putString(KEY_LANGUAGE, value).apply()

    /** Theme id, see Themes.kt. */
    var themeId: String
        get() = store.getString(KEY_THEME, "ivory") ?: "ivory"
        set(value) = store.edit().putString(KEY_THEME, value).apply()

    /** True = bigger labels on the app tiles. */
    var largeText: Boolean
        get() = store.getBoolean(KEY_LARGE, false)
        set(value) = store.edit().putBoolean(KEY_LARGE, value).apply()

    private companion object {
        const val KEY_LANGUAGE = "language"
        const val KEY_THEME = "theme"
        const val KEY_LARGE = "large_text"
    }
}
