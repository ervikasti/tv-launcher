package com.sahaj.launcher.i18n

/**
 * Every piece of text shown on screen has a short NAME ("key") listed here.
 *
 * You normally do NOT need to edit this file. To change wording, edit TextMap.kt.
 * Only add a line here when you add a brand new text to the app.
 */
object Keys {
    // ---- Top bar: internet status (top-left) ----
    const val NET_CONNECTED = "net_connected"
    const val NET_OK = "net_ok"
    const val NET_OFFLINE = "net_offline"
    const val NET_OFFLINE_HINT = "net_offline_hint"

    // ---- Top bar: settings button (top-right) ----
    const val SETTINGS = "settings"

    // ---- Clock: label next to the time (AM/PM style) ----
    const val PERIOD_MORNING = "period_morning"
    const val PERIOD_AFTERNOON = "period_afternoon"
    const val PERIOD_EVENING = "period_evening"
    const val PERIOD_NIGHT = "period_night"

    // ---- App tiles: name + one-line description ----
    const val APP_YOUTUBE_NAME = "app_youtube_name"
    const val APP_YOUTUBE_DESC = "app_youtube_desc"
    const val APP_NETFLIX_NAME = "app_netflix_name"
    const val APP_NETFLIX_DESC = "app_netflix_desc"
    const val APP_LIVETV_NAME = "app_livetv_name"
    const val APP_LIVETV_DESC = "app_livetv_desc"
    const val APP_JIOHOTSTAR_NAME = "app_jiohotstar_name"
    const val APP_JIOHOTSTAR_DESC = "app_jiohotstar_desc"
    const val APP_MUSIC_NAME = "app_music_name"
    const val APP_MUSIC_DESC = "app_music_desc"

    // ---- Pop-ups: app not installed / network info ----
    const val NOT_INSTALLED_BODY = "not_installed_body"
    const val NET_DIALOG_TITLE = "net_dialog_title"
    const val NET_DIALOG_BODY = "net_dialog_body"
    const val BACK_HOME = "back_home"

    // ---- Settings pop-up ----
    const val SETTINGS_TITLE = "settings_title"
    const val TAB_THEME = "tab_theme"
    const val TAB_GENERAL = "tab_general"
    const val THEME_HINT = "theme_hint"
    const val THEME_CURRENT = "theme_current"       // contains %s = theme name
    const val THEME_IVORY = "theme_ivory"
    const val THEME_SAGE = "theme_sage"
    const val THEME_SKY = "theme_sky"
    const val THEME_SAND = "theme_sand"
    // ---- Settings: "Choose apps" tab ----
    const val TAB_APPS = "tab_apps"
    const val APPS_HINT = "apps_hint"
    const val APPS_COUNT = "apps_count"             // contains two %d = chosen / maximum
    const val APPS_RESET = "apps_reset"
    const val APPS_NOT_INSTALLED = "apps_not_installed"
    const val APP_GENERIC_DESC = "app_generic_desc" // description under apps the viewer picked
    // ---- Settings: "TV Settings" tab ----
    const val TAB_TV_SETTINGS = "tab_tv_settings"
    const val TV_SETTINGS_HINT = "tv_settings_hint"
    const val TV_SETTINGS_OPEN_ALL = "tv_settings_open_all"
    const val TV_SETTINGS_OPEN_WIFI = "tv_settings_open_wifi"
    const val TV_SETTINGS_OPEN_DISPLAY = "tv_settings_open_display"
    const val TV_SETTINGS_OPEN_APPS = "tv_settings_open_apps"
    const val TV_SETTINGS_OPEN_DEVICE = "tv_settings_open_device"
    const val LANGUAGE_LABEL = "language_label"
    const val LANG_HINDI = "lang_hindi"
    const val LANG_ENGLISH = "lang_english"
    const val LARGE_TEXT = "large_text"
    const val PREFS_SAVED = "prefs_saved"
}
