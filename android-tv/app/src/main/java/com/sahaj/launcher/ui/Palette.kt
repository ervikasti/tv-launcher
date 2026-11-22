package com.sahaj.launcher.ui

import android.graphics.Color

/** Fixed colours used by the screens (theme-dependent colours are in config/Themes.kt). */
object Palette {
    val TEXT = Color.parseColor("#344237")          // main text
    val TEXT_SOFT = Color.parseColor("#626b5c")     // date, descriptions
    val TEXT_MUTED = Color.parseColor("#65705d")    // small notes
    val CLOCK_PERIOD = Color.parseColor("#64705c")  // "सुबह" next to time
    val ICON_GREEN = Color.parseColor("#4f6848")    // wifi icon
    val ICON_CIRCLE = Color.parseColor("#e7ecdf")   // circle behind wifi icon
    val ONLINE_DOT = Color.parseColor("#5b774d")    // green dot = connected
    val OFFLINE_DOT = Color.parseColor("#b4372b")   // red dot = not connected
    val OUTLINE = Color.parseColor("#dce0d2")       // thin border of settings button
    val FOCUS_RING = Color.parseColor("#536b4b")    // ring around a focused button
    val FOCUS_FILL = Color.parseColor("#e6ebdd")    // light fill of a focused button
    val TILE_SELECTED_BORDER = Color.parseColor("#748767")
    val TILE_FOCUS_RING = Color.parseColor("#879873")
    val BADGE = Color.parseColor("#4f6546")         // round tick on selected tile + main buttons
    val CLOSE_BG = Color.parseColor("#eaece2")
    val TAB_TRACK = Color.parseColor("#e8ebdf")
    val TAB_TEXT = Color.parseColor("#54614c")
    val CHOICE_SELECTED_FILL = Color.parseColor("#e2e9d9")
    val CHOICE_SELECTED_BORDER = Color.parseColor("#6f805f")
    val CHOICE_BORDER = Color.parseColor("#d8ddce")
    val THEME_BORDER = Color.parseColor("#d3d9cb")
    val THEME_SELECTED_BORDER = Color.parseColor("#526b46")
    val TOGGLE_ROW = Color.parseColor("#eceee4")
    val TOGGLE_OFF = Color.parseColor("#929987")
}
