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
import java.util.ArrayList

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
    private var selectedIndex = getDefaultSelectedIndex(initialApps)
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
            onFocusChanged = { applySelection() },
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

    /** Finds the default index: YouTube if present, otherwise the center app tile. */
    private fun getDefaultSelectedIndex(appList: List<AppEntry>): Int {
        if (appList.isEmpty()) return 0
        var i = 0
        while (i < appList.size) {
            val entry = appList[i]
            if (entry.id == "youtube") return i
            var j = 0
            while (j < entry.packages.size) {
                if ((entry.packages[j] as java.lang.String).indexOf("youtube") >= 0) return i
                j++
            }
            i++
        }
        return appList.size / 2
    }

    /** Moves the remote's focus to the currently selected app tile. */
    fun focusSelectedTile() {
        val target: View? = if (selectedIndex >= 0 && selectedIndex < tiles.size) tiles[selectedIndex] else if (tiles.size > 0) tiles[0] else null
        if (target != null) {
            target.requestFocus()
            scroller.requestChildFocus(row, target)
        }
    }

    /** Resets the selection to YouTube (or center app) and moves remote focus to it. */
    fun resetFocusToDefault() {
        selectedIndex = getDefaultSelectedIndex(apps)
        applySelection()
        focusSelectedTile()
    }

    /**
     * Replaces the tiles with a new list of apps (the viewer changed the choice in Settings).
     * YouTube app is focused if available, otherwise center app.
     */
    fun setApps(newApps: List<AppEntry>) {
        if (newApps.isEmpty()) return
        apps = newApps
        selectedIndex = getDefaultSelectedIndex(newApps)
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
        center.setPadding(0, context.dp(8), 0, context.dp(8))

        // Title: name of the selected app
        titleView.setTextColor(Palette.TEXT)
        titleView.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        titleView.gravity = Gravity.CENTER
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
        selectionBinder.bind(titleView) {
            if (selectedIndex >= 0 && selectedIndex < apps.size) apps[selectedIndex].displayName() else ""
        }
        val titleParams = LinearLayout.LayoutParams(MATCH, WRAP)
        titleParams.leftMargin = context.dp(48)
        titleParams.rightMargin = context.dp(48)
        center.addView(titleView, titleParams)

        // Description of the selected app
        descView.setTextColor(Palette.TEXT_SOFT)
        descView.gravity = Gravity.CENTER
        descView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        selectionBinder.bind(descView) {
            val key: String? = if (selectedIndex >= 0 && selectedIndex < apps.size) apps[selectedIndex].descKey else null
            if (key != null && key.length > 0) Translator.t(key) else ""
        }
        val descParams = LinearLayout.LayoutParams(MATCH, WRAP)
        descParams.topMargin = context.dp(4)
        descParams.leftMargin = context.dp(48)
        descParams.rightMargin = context.dp(48)
        center.addView(descView, descParams)

        // The row of tiles sits inside a sideways scroller (needed when more than 5 apps are chosen).
        // With 5 or fewer apps the tiles are simply centred.
        scroller.isFocusable = false                 // focus goes to the tiles, not the scroller
        scroller.isHorizontalScrollBarEnabled = false
        scroller.overScrollMode = View.OVER_SCROLL_NEVER
        scroller.isFillViewport = true
        scroller.clipChildren = false                // prevents clipping of grown tiles & pointer button
        scroller.clipToPadding = false
        scroller.setPadding(context.dp(38), 0, context.dp(38), 0)

        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_HORIZONTAL
        row.clipChildren = false                     // prevents clipping of grown tiles & pointer button
        row.clipToPadding = false
        // Top space: room for the grown tile. Bottom space: room for the round "press OK" button.
        row.setPadding(0, context.dp(14), 0, context.dp(76))
        scroller.addView(row, FrameLayout.LayoutParams(WRAP, WRAP))

        val scrollerParams = LinearLayout.LayoutParams(MATCH, WRAP)
        scrollerParams.topMargin = context.dp(10)
        center.addView(scroller, scrollerParams)
        return center
    }

    /** (Re)creates all tiles from [apps]. */
    private fun fillRow() {
        row.removeAllViews()
        tiles.clear()
        tileBinder = TextBinder()   // drop the text rules of the old tiles

        // Usable width on screen inside scroller (subtracting 38dp side padding from screen width)
        val displayWidth = context.resources.displayMetrics.widthPixels
        val sidePaddingPx = context.dp(38) * 2
        val usableWidth = Math.max(1, displayWidth - sidePaddingPx)
        val slotWidth = usableWidth / 5
        val marginPx = context.dp(10)
        val tileWidth = Math.max(context.dp(80), slotWidth - marginPx * 2)

        var index = 0
        while (index < apps.size) {
            val entry = apps[index]
            val tile = buildTile(index, entry)
            tile.setPointerColor(pointerColor)
            tile.setLargeText(largeText)
            tiles.add(tile)
            val params = LinearLayout.LayoutParams(tileWidth, context.dp(LauncherConfig.TILE_HEIGHT_DP))
            params.leftMargin = marginPx
            params.rightMargin = marginPx
            row.addView(tile, params)
            index++
        }
    }

    /** Creates one tile and connects its remote-control behaviour. */
    private fun buildTile(index: Int, entry: AppEntry): AppTileView {
        val tile = AppTileView(context, entry, tileBinder)

        // Moving the cursor onto a tile selects it and brings it into view inside the scroller.
        tile.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                select(index)
                scroller.requestChildFocus(row, tile)
            } else {
                applySelection()
            }
        }
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
                    if (index == 0) {
                        val lastTile: AppTileView? = if (tiles.size > 0) tiles[tiles.size - 1] else null
                        if (lastTile != null) {
                            lastTile.requestFocus()
                            scroller.requestChildFocus(row, lastTile)
                        }
                        true
                    } else false
                KeyEvent.KEYCODE_DPAD_RIGHT ->
                    if (index == tiles.size - 1) {
                        val firstTile: AppTileView? = if (tiles.size > 0) tiles[0] else null
                        if (firstTile != null) {
                            firstTile.requestFocus()
                            scroller.requestChildFocus(row, firstTile)
                        }
                        true
                    } else false
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
        selectedIndex = index
        applySelection()
    }

    fun applySelection() {
        var hasTileFocus = false
        var i = 0
        while (i < tiles.size) {
            if (tiles[i].hasFocus()) {
                hasTileFocus = true
                break
            }
            i++
        }
        i = 0
        while (i < tiles.size) {
            tiles[i].setSelectedLook(hasTileFocus && i == selectedIndex)
            i++
        }
        selectionBinder.refresh()
    }
}
