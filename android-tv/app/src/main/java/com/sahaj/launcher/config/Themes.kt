package com.sahaj.launcher.config

import android.graphics.Color
import com.sahaj.launcher.i18n.Keys

/**
 * One colour theme. Colours are written as "#RRGGBB" so they are easy to change.
 *  - background : whole screen colour
 *  - surface    : colour of pop-up cards
 *  - accent     : colour of the round "press OK" button under the selected tile
 *  - glow/wash  : two soft colour clouds drawn behind everything
 */
data class LauncherTheme(
    val id: String,
    val nameKey: String,   // text key for the theme name (see TextMap.kt)
    val background: Int,
    val surface: Int,
    val accent: Int,
    val glow: Int,
    val wash: Int,
)

object Themes {
    val all: List<LauncherTheme> = listOf(
        LauncherTheme(
            id = "ivory", nameKey = Keys.THEME_IVORY,
            background = Color.parseColor("#f7f6ef"), surface = Color.parseColor("#faf8f2"),
            accent = Color.parseColor("#405b36"), glow = Color.parseColor("#e7ebd9"), wash = Color.parseColor("#ebe6d9"),
        ),
        LauncherTheme(
            id = "sage", nameKey = Keys.THEME_SAGE,
            background = Color.parseColor("#e8efe5"), surface = Color.parseColor("#f2f6ef"),
            accent = Color.parseColor("#36543e"), glow = Color.parseColor("#cedfc6"), wash = Color.parseColor("#e5ecdb"),
        ),
        LauncherTheme(
            id = "sky", nameKey = Keys.THEME_SKY,
            background = Color.parseColor("#e8eff5"), surface = Color.parseColor("#f2f6fa"),
            accent = Color.parseColor("#365772"), glow = Color.parseColor("#d0e3ef"), wash = Color.parseColor("#e5e8f2"),
        ),
        LauncherTheme(
            id = "sand", nameKey = Keys.THEME_SAND,
            background = Color.parseColor("#f2e8db"), surface = Color.parseColor("#faf4eb"),
            accent = Color.parseColor("#725339"), glow = Color.parseColor("#e8d6bb"), wash = Color.parseColor("#efdfd2"),
        ),
    )

    /** Finds a theme by id; falls back to the first theme if the id is unknown. */
    fun byId(id: String): LauncherTheme = all.firstOrNull { it.id == id } ?: all[0]
}
