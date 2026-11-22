package com.sahaj.launcher.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.sahaj.launcher.config.AppEntry
import com.sahaj.launcher.config.LauncherConfig
import com.sahaj.launcher.config.LauncherTheme
import com.sahaj.launcher.config.displayName
import com.sahaj.launcher.i18n.TextBinder
import com.sahaj.launcher.i18n.Translator
import com.sahaj.launcher.util.NetworkUtil

/**
 * The main home screen: top bar, big app title, and the row of app tiles.
 * It only DRAWS and reports button presses through [Callbacks]; what happens
 * next (open app, open settings...) is decided in MainActivity.
 *
 * The apps shown come from the list given in the constructor and can be
 * replaced at any time with [setApps] (when the viewer picks apps in Settings).
 */
class HomeScreen(
    private val context: Context,
    initialApps: List<AppEntry>,
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

    private var apps: List<AppEntry> = initialApps
    private var selectedIndex = minOf(LauncherConfig.DEFAULT_SELECTED_INDEX, initialApps.lastIndex)
    private var largeText = false
    private var pointerColor = Color.parseColor("#405b36")

    private val binder = TextBinder()           // top bar texts (clock, status...)
    private val selectionBinder = TextBinder()  // big title + description of the selected app
    private var tileBinder = TextBinder()       // app names inside tiles (new one after every setApps)
    private val tiles = ArrayList<AppTileView>()

    private val glowLeft = ImageView(context)
    private val glowRight = ImageView(context)
    private val titleView = TextView(context)
    private val descView = TextView(context)
    private val scroller = HorizontalScrollView(context)   // lets the row scroll sideways
    private val row = LinearLayout(context)                // holds the tiles
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

        fillRow()
        applySelection()
    }

    // ---------------------------------------------------------- public API

    /** Colours the screen with the chosen theme. */
    fun applyTheme(theme: LauncherTheme) {
        view.setBackgroundColor(theme.background)
        glowLeft.setImageDrawable(Ui.glow(theme.glow, context.dp(345)))
        glowRight.setImageDrawable(Ui.glow(theme.wash, context.dp(405)))
        pointerColor = theme.accent
        for (tile in tiles) tile.setPointerColor(pointerColor)
    }

    /** Switches bigger / normal text on the title and tile labels. */
    fun applyLargeText(large: Boolean) {
        largeText = large
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (large) 34f else 28f)
        descView.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (large) 16f else 14f)
        for (tile in tiles) tile.setLargeText(large)
    }

    /** Call after the language changed so every text is redrawn. */
    fun onLanguageChanged() {
        binder.refresh()
        tileBinder.refresh()
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

    /**
     * Replaces the tiles with a new list of apps (the viewer changed the choice in Settings).
     * The previously selected app stays selected if it is still in the list.
     */
    fun setApps(newApps: List<AppEntry>) {
        if (newApps.isEmpty()) return
        val previousId = apps.getOrNull(selectedIndex)?.id
        apps = newApps
        val kept = newApps.indexOfFirst { it.id == previousId }
        selectedIndex = if (kept >= 0) kept else minOf(LauncherConfig.DEFAULT_SELECTED_INDEX, newApps.lastIndex)
        fillRow()
        scroller.scrollTo(0, 0)
        applySelection()
    }

    // ------------------------------------------------------------- building

    /** Big title + description + the (scrolling) row of tiles. */
    private fun buildCenter(): View {
        val center = LinearLayout(context)
        center.orientation = LinearLayout.VERTICAL
        center.gravity = Gravity.CENTER
        center.clipChildren = false
        center.clipToPadding = false
        center.setPadding(context.dp(48), context.dp(8), context.dp(48), context.dp(8))

        // Title: name of the selected app
        titleView.setTextColor(Palette.TEXT)
        titleView.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        titleView.gravity = Gravity.CENTER
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
        selectionBinder.bind(titleView) { apps[selectedIndex].displayName() }
        center.addView(titleView, LinearLayout.LayoutParams(MATCH, WRAP))

        // Description of the selected app
        descView.setTextColor(Palette.TEXT_SOFT)
        descView.gravity = Gravity.CENTER
        descView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        selectionBinder.bind(descView) { Translator.t(apps[selectedIndex].descKey) }
        val descParams = LinearLayout.LayoutParams(MATCH, WRAP)
        descParams.topMargin = context.dp(4)
        center.addView(descView, descParams)

        // The row of tiles sits inside a sideways scroller (needed when more than 5 apps are chosen).
        // With 5 or fewer apps the tiles are simply centred.
        scroller.isFocusable = false                 // focus goes to the tiles, not the scroller
        scroller.isHorizontalScrollBarEnabled = false
        scroller.overScrollMode = View.OVER_SCROLL_NEVER
        scroller.isFillViewport = true
        scroller.clipToPadding = false
        scroller.setPadding(context.dp(38), 0, context.dp(38), 0)

        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_HORIZONTAL
        row.clipChildren = false
        // Top space: room for the grown tile. Bottom space: room for the round "press OK" button.
        row.setPadding(0, context.dp(14), 0, context.dp(76))
        scroller.addView(row, FrameLayout.LayoutParams(WRAP, WRAP))

        // Screen-wide scroller: cancel the centre's side padding so tiles can reach the screen edge.
        val scrollerParams = LinearLayout.LayoutParams(MATCH, WRAP)
        scrollerParams.topMargin = context.dp(10)
        scrollerParams.leftMargin = -context.dp(48)
        scrollerParams.rightMargin = -context.dp(48)
        center.addView(scroller, scrollerParams)
        return center
    }

    /** (Re)creates all tiles from [apps]. */
    private fun fillRow() {
        row.removeAllViews()
        tiles.clear()
        tileBinder = TextBinder()   // drop the text rules of the old tiles

        // Every tile has the same width: one fifth of the usable screen width.
        val slot = (context.resources.displayMetrics.widthPixels - 2 * context.dp(48)) / 5
        val tileWidth = slot - 2 * context.dp(10)

        apps.forEachIndexed { index, entry ->
            val tile = buildTile(index, entry)
            tile.setPointerColor(pointerColor)
            tile.setLargeText(largeText)
            tiles.add(tile)
            val params = LinearLayout.LayoutParams(tileWidth, context.dp(LauncherConfig.TILE_HEIGHT_DP))
            params.leftMargin = context.dp(10)
            params.rightMargin = context.dp(10)
            row.addView(tile, params)
        }
    }

    /** Creates one tile and connects its remote-control behaviour. */
    private fun buildTile(index: Int, entry: AppEntry): AppTileView {
        val tile = AppTileView(context, entry, tileBinder)

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
