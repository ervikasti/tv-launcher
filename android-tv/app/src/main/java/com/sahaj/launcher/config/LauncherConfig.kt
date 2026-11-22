package com.sahaj.launcher.config

/**
 * Simple knobs you can change without touching any other code.
 * (All sizes are in "dp", the TV-independent size unit.)
 */
object LauncherConfig {
    /** Which app tile is selected when the launcher opens (0 = first). */
    const val DEFAULT_SELECTED_INDEX = 0

    /**
     * Length of the tile "grow" animation in milliseconds.
     * Set to 0 to turn animations OFF completely (best for very slow TVs).
     */
    const val ANIMATION_MS = 120L

    /** Most apps that can be shown on the home row (the row scrolls sideways after 5). */
    const val MAX_APPS = 12

    /** Height of each app tile. */
    const val TILE_HEIGHT_DP = 150

    /** Height of the area that holds the logo inside a tile. */
    const val LOGO_BOX_HEIGHT_DP = 72
}
