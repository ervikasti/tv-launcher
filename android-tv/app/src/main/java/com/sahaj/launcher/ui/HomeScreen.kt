package com.sahaj.launcher.ui

import android.content.Context
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.sahaj.launcher.config.AppCatalog
import com.sahaj.launcher.config.AppEntry
import com.sahaj.launcher.config.LauncherConfig
import com.sahaj.launcher.config.LauncherTheme
import com.sahaj.launcher.i18n.TextBinder
import com.sahaj.launcher.i18n.Translator
import com.sahaj.launcher.util.NetworkUtil

/**
 * The main home screen: top bar, big app title, and the row of app tiles.
 * It only DRAWS and reports button presses through [Callbacks]; what happens
 * next (open app, open settings...) is decided in MainActivity.
 */
class HomeScreen(
    private val context: Context,
    private val callbacks: Callbacks,
) {
    /** What the screen asks the activity to do. */
    interface Callbacks {
        fun onOpenNetwork()
        fun onOpenSettings()
        fun onLaunch(entry: AppEntry)
    }

    /** Add this to the activity. */
    val view = FrameLayout(context)

    private val binder = TextBinder()           // texts that follow language + clock
    private val selectionBinder = TextBinder()  // big title + description of the selected app
    private val tiles = ArrayList<AppTileView>()
    private var selectedIndex = LauncherConfig.DEFAULT_SELECTED_INDEX

    private val glowLeft = ImageView(context)
    private val glowRight = ImageView(context)
    private val titleView = TextView(context)
    private val descView = TextView(context)
    private val header: HeaderBar

    init {
        view.clipChildren = false

        // Two soft colour clouds behind everything (cheap, no blur).
        val leftParams = FrameLayout.LayoutParams(context.dp(345), context.dp(345), Gravity.START or Gravity.TOP)
        leftParams.leftMargin = -context.dp(120)
        leftParams.topMargin = context.dp(180)
        view.addView(glowLeft, leftParams)
        val rightParams = FrameLayout.LayoutParams(context.dp(405), context.dp(405), Gravity.END or Gravity.TOP)
        rightParams.rightMargin = -context.dp(120)
        rightParams.topMargin = context.dp(36)
        view.addView(glowRight, rightParams)

        header = HeaderBar(
            context = context,
            binder = binder,
            onNetworkClick = { callbacks.onOpenNetwork() },
            onSettingsClick = { callbacks.onOpenSettings() },
            onMoveDown = { focusSelectedTile() },
        )

        // Vertical stack: top bar, then the centre area that fills the rest.
        val column = LinearLayout(context)
        column.orientation = LinearLayout.VERTICAL
        column.clipChildren = false
        column.clipToPadding = false
        column.addView(header.view, LinearLayout.LayoutParams(MATCH, WRAP))
        column.addView(buildCenter(), LinearLayout.LayoutParams(MATCH, 0, 1f))
        view.addView(column, FrameLayout.LayoutParams(MATCH, MATCH))

        applySelection()
    }

    // ---------------------------------------------------------- public API

    /** Colours the screen with the chosen theme. */
    fun applyTheme(theme: LauncherTheme) {
        view.setBackgroundColor(theme.background)
        glowLeft.setImageDrawable(Ui.glow(theme.glow, context.dp(345)))
        glowRight.setImageDrawable(Ui.glow(theme.wash, context.dp(405)))
        for (tile in tiles) tile.setPointerColor(theme.accent)
    }

    /** Switches bigger / normal text on the title and tile labels. */
    fun applyLargeText(large: Boolean) {
        titleView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, if (large) 34f else 28f)
        descView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, if (large) 16f else 14f)
        for (tile in tiles) tile.setLargeText(large)
    }

    /** Call after the language changed so every text is redrawn. */
    fun onLanguageChanged() {
        binder.refresh()
        selectionBinder.refresh()
    }

    /** Called every minute (and on resume): updates clock, date and internet status. */
    fun refreshClockAndNetwork() {
        header.setOnline(NetworkUtil.isOnline(context))
        binder.refresh()
    }

    /** Moves the remote's focus to the currently selected app tile. */
    fun focusSelectedTile() {
        tiles.getOrNull(selectedIndex)?.requestFocus()
    }

    // ------------------------------------------------------------- building

    /** Big title + description + the row of tiles. */
    private fun buildCenter(): View {
        val center = LinearLayout(context)
        center.orientation = LinearLayout.VERTICAL
        center.gravity = Gravity.CENTER
        center.clipChildren = false
        center.clipToPadding = false
        center.setPadding(context.dp(48), context.dp(8), context.dp(48), context.dp(8))

        // Title: name of the selected app
        titleView.setTextColor(Palette.TEXT)
        titleView.typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
        titleView.gravity = Gravity.CENTER
        titleView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 28f)
        selectionBinder.bind(titleView) { Translator.t(AppCatalog.apps[selectedIndex].nameKey) }
        center.addView(titleView, LinearLayout.LayoutParams(MATCH, WRAP))

        // Description of the selected app
        descView.setTextColor(Palette.TEXT_SOFT)
        descView.gravity = Gravity.CENTER
        descView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14f)
        selectionBinder.bind(descView) { Translator.t(AppCatalog.apps[selectedIndex].descKey) }
        val descParams = LinearLayout.LayoutParams(MATCH, WRAP)
        descParams.topMargin = context.dp(4)
        center.addView(descView, descParams)

        // Row of tiles
        val row = LinearLayout(context)
        row.orientation = LinearLayout.HORIZONTAL
        row.clipChildren = false
        row.clipToPadding = false
        AppCatalog.apps.forEachIndexed { index, entry ->
            val tile = buildTile(index, entry)
            tiles.add(tile)
            val params = LinearLayout.LayoutParams(0, context.dp(LauncherConfig.TILE_HEIGHT_DP), 1f)
            params.leftMargin = context.dp(10)
            params.rightMargin = context.dp(10)
            row.addView(tile, params)
        }
        val rowParams = LinearLayout.LayoutParams(MATCH, WRAP)
        rowParams.topMargin = context.dp(24)
        center.addView(row, rowParams)

        // Empty space under the tiles so the "press OK" button has room.
        center.addView(View(context), LinearLayout.LayoutParams(MATCH, context.dp(72)))
        return center
    }

    /** Creates one tile and connects its remote-control behaviour. */
    private fun buildTile(index: Int, entry: AppEntry): AppTileView {
        val tile = AppTileView(context, entry, binder)

        // Moving the cursor onto a tile selects it.
        tile.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) select(index) }
        // OK button / tap opens the app.
        tile.setOnClickListener {
            select(index)
            callbacks.onLaunch(entry)
        }
        // LEFT on first tile jumps to last, RIGHT on last jumps to first; UP goes to Settings.
        tile.setOnKeyListener { _, keyCode, event ->
            if (event.action != KeyEvent.ACTION_DOWN) {
                false
            } else when (keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT ->
                    if (index == 0) { tiles.last().requestFocus(); true } else false
                KeyEvent.KEYCODE_DPAD_RIGHT ->
                    if (index == tiles.lastIndex) { tiles.first().requestFocus(); true } else false
                KeyEvent.KEYCODE_DPAD_UP -> {
                    header.settingsButton.requestFocus()
                    true
                }
                else -> false
            }
        }
        return tile
    }

    /** Marks tile [index] as the selected one and updates the big title. */
    private fun select(index: Int) {
        if (index == selectedIndex) return
        selectedIndex = index
        applySelection()
    }

    private fun applySelection() {
        tiles.forEachIndexed { i, tile -> tile.setSelectedLook(i == selectedIndex) }
        selectionBinder.refresh()
    }
}
