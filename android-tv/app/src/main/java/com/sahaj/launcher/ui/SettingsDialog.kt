package com.sahaj.launcher.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.sahaj.launcher.R
import com.sahaj.launcher.config.LauncherTheme
import com.sahaj.launcher.config.Themes
import com.sahaj.launcher.data.Prefs
import com.sahaj.launcher.i18n.Keys
import com.sahaj.launcher.i18n.Lang
import com.sahaj.launcher.i18n.TextBinder
import com.sahaj.launcher.i18n.Translator

/**
 * The Settings pop-up with two tabs:
 *   1. "Look & feel"      - choose a colour theme
 *   2. "Language & text"  - choose Hindi / English and larger labels
 * Changes apply immediately and are saved through [Prefs].
 */
class SettingsDialog(
    private val context: Context,
    private val prefs: Prefs,
    private val listener: Listener,
) {
    /** Tells the home screen to redraw after a change. */
    interface Listener {
        fun onLanguageChanged()
        fun onThemeChanged()
        fun onLargeTextChanged()
    }

    val dialog = Dialog(context, R.style.Theme_Sahaj_Dialog)

    private val binder = TextBinder()          // texts inside this pop-up
    private val themeCards = ArrayList<ThemeCard>()
    private val languageButtons = ArrayList<Pair<String, LinearLayout>>()
    private val languageChecks = ArrayList<ImageView>()

    private lateinit var card: ScrollView
    private lateinit var tabTheme: TextView
    private lateinit var tabGeneral: TextView
    private lateinit var themePanel: View
    private lateinit var generalPanel: View
    private lateinit var toggleTrack: LinearLayout
    private lateinit var tabTrack: LinearLayout
    private var showingThemeTab = true

    /** One theme choice in the grid. */
    private class ThemeCard(val theme: LauncherTheme, val root: LinearLayout, val check: ImageView)

    init {
        dialog.setContentView(buildRoot())
        dialog.window?.setLayout(MATCH, MATCH)
        dialog.setOnShowListener { tabTheme.requestFocus() }
        updateTabs()
        updateThemeCards()
        updateLanguageButtons()
        updateToggle()
    }

    fun show() = dialog.show()

    // ------------------------------------------------------------ structure

    private fun buildRoot(): View {
        val root = FrameLayout(context)
        root.setOnClickListener { dialog.dismiss() }   // tap outside closes (touch only)

        card = ScrollView(context)
        card.isClickable = true
        card.isVerticalScrollBarEnabled = false
        card.overScrollMode = View.OVER_SCROLL_NEVER
        card.clipToOutline = true
        card.background = Ui.shape(context, Themes.byId(prefs.themeId).surface, 28)

        val column = LinearLayout(context)
        column.orientation = LinearLayout.VERTICAL
        column.setPadding(context.dp(28), context.dp(24), context.dp(28), context.dp(24))
        column.addView(buildTitleRow(), LinearLayout.LayoutParams(MATCH, WRAP))
        column.addView(buildTabs(), marginTop(20))

        themePanel = buildThemePanel()
        generalPanel = buildGeneralPanel()
        column.addView(themePanel, marginTop(16))
        column.addView(generalPanel, marginTop(16))

        val note = Ui.text(context, 12f, Palette.TEXT_MUTED)
        binder.bind(note, Keys.PREFS_SAVED)
        column.addView(note, marginTop(18))
        card.addView(column)

        val cardParams = FrameLayout.LayoutParams(context.dp(560), WRAP, Gravity.CENTER)
        cardParams.topMargin = context.dp(20)
        cardParams.bottomMargin = context.dp(20)
        root.addView(card, cardParams)
        return root
    }

    /** Title on the left, round close (X) button on the right. */
    private fun buildTitleRow(): View {
        val row = LinearLayout(context)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_VERTICAL

        val title = Ui.text(context, 22f, Palette.TEXT, medium = true)
        binder.bind(title, Keys.SETTINGS_TITLE)
        row.addView(title, LinearLayout.LayoutParams(0, WRAP, 1f))

        val close = FrameLayout(context)
        Ui.makeFocusable(close)
        close.background = Ui.focusStates(
            Ui.circle(Palette.CLOSE_BG),
            ringCircle(Palette.CLOSE_BG),
        )
        close.addView(
            Ui.icon(context, R.drawable.ic_close, Palette.TEXT),
            FrameLayout.LayoutParams(context.dp(22), context.dp(22), Gravity.CENTER),
        )
        close.setOnClickListener { dialog.dismiss() }
        row.addView(close, LinearLayout.LayoutParams(context.dp(44), context.dp(44)))
        return row
    }

    /** The two tab buttons. */
    private fun buildTabs(): View {
        tabTrack = LinearLayout(context)
        tabTrack.orientation = LinearLayout.HORIZONTAL
        tabTrack.setPadding(context.dp(6), context.dp(6), context.dp(6), context.dp(6))
        tabTrack.background = Ui.shape(context, Palette.TAB_TRACK, 16)

        tabTheme = buildTab(Keys.TAB_THEME) { showingThemeTab = true; updateTabs() }
        tabGeneral = buildTab(Keys.TAB_GENERAL) { showingThemeTab = false; updateTabs() }

        val left = LinearLayout.LayoutParams(0, context.dp(44), 1f)
        left.rightMargin = context.dp(4)
        val right = LinearLayout.LayoutParams(0, context.dp(44), 1f)
        right.leftMargin = context.dp(4)
        tabTrack.addView(tabTheme, left)
        tabTrack.addView(tabGeneral, right)
        return tabTrack
    }

    private fun buildTab(key: String, onClick: () -> Unit): TextView {
        val tab = Ui.text(context, 14f, Palette.TAB_TEXT, medium = true)
        tab.gravity = Gravity.CENTER
        Ui.makeFocusable(tab)
        binder.bind(tab, key)
        tab.setOnClickListener { onClick() }
        return tab
    }

    // ---------------------------------------------------------- theme panel

    private fun buildThemePanel(): View {
        val panel = LinearLayout(context)
        panel.orientation = LinearLayout.VERTICAL

        val hint = Ui.text(context, 13f, Palette.TEXT_MUTED)
        binder.bind(hint, Keys.THEME_HINT)
        panel.addView(hint, LinearLayout.LayoutParams(MATCH, WRAP))

        // 2 columns grid of theme cards
        var row: LinearLayout? = null
        Themes.all.forEachIndexed { index, theme ->
            if (index % 2 == 0) {
                row = LinearLayout(context)
                row!!.orientation = LinearLayout.HORIZONTAL
                panel.addView(row, marginTop(10))
            }
            val themeCard = buildThemeCard(theme)
            themeCards.add(themeCard)
            val params = LinearLayout.LayoutParams(0, WRAP, 1f)
            if (index % 2 == 0) params.rightMargin = context.dp(6) else params.leftMargin = context.dp(6)
            row!!.addView(themeCard.root, params)
        }

        val current = Ui.text(context, 13f, Palette.TEXT_MUTED)
        binder.bind(current) { Translator.t(Keys.THEME_CURRENT, Translator.t(Themes.byId(prefs.themeId).nameKey)) }
        panel.addView(current, marginTop(14))
        return panel
    }

    private fun buildThemeCard(theme: LauncherTheme): ThemeCard {
        val root = LinearLayout(context)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(context.dp(10), context.dp(10), context.dp(10), context.dp(10))
        Ui.makeFocusable(root)

        // Preview strip: theme background with three colour chips
        val preview = LinearLayout(context)
        preview.orientation = LinearLayout.HORIZONTAL
        preview.gravity = Gravity.CENTER
        preview.background = Ui.shape(context, theme.background, 8)
        for (chip in listOf(theme.accent, Color.parseColor("#e0e8d9"), Color.parseColor("#e7e7f2"))) {
            val view = View(context)
            view.background = Ui.shape(context, chip, 6)
            val params = LinearLayout.LayoutParams(context.dp(28), context.dp(20))
            params.leftMargin = context.dp(4)
            params.rightMargin = context.dp(4)
            preview.addView(view, params)
        }
        root.addView(preview, LinearLayout.LayoutParams(MATCH, context.dp(40)))

        // Name + tick
        val line = LinearLayout(context)
        line.orientation = LinearLayout.HORIZONTAL
        line.gravity = Gravity.CENTER_VERTICAL
        val name = Ui.text(context, 14f, Palette.TEXT, medium = true)
        binder.bind(name, theme.nameKey)
        line.addView(name, LinearLayout.LayoutParams(0, WRAP, 1f))
        val check = Ui.icon(context, R.drawable.ic_check, Palette.TEXT)
        line.addView(check, LinearLayout.LayoutParams(context.dp(20), context.dp(20)))
        root.addView(line, marginTop(8))

        root.setOnClickListener {
            prefs.themeId = theme.id
            card.background = Ui.shape(context, theme.surface, 28)
            updateThemeCards()
            binder.refresh()              // "Current theme: ..." line
            listener.onThemeChanged()
        }
        return ThemeCard(theme, root, check)
    }

    /** Re-draws borders and ticks so the chosen theme is marked. */
    private fun updateThemeCards() {
        for (item in themeCards) {
            val chosen = item.theme.id == prefs.themeId
            val border = if (chosen) Palette.THEME_SELECTED_BORDER else Palette.THEME_BORDER
            item.root.background = Ui.focusStates(
                Ui.shape(context, item.theme.surface, 16, border, 2),
                Ui.shape(context, item.theme.surface, 16, Palette.FOCUS_RING, 4),
            )
            item.check.visibility = if (chosen) View.VISIBLE else View.INVISIBLE
        }
    }

    // -------------------------------------------------------- general panel

    private fun buildGeneralPanel(): View {
        val panel = LinearLayout(context)
        panel.orientation = LinearLayout.VERTICAL

        val label = Ui.text(context, 15f, Palette.TEXT, medium = true)
        binder.bind(label, Keys.LANGUAGE_LABEL)
        panel.addView(label, LinearLayout.LayoutParams(MATCH, WRAP))

        // Language buttons side by side
        val row = LinearLayout(context)
        row.orientation = LinearLayout.HORIZONTAL
        val options = listOf(Lang.HI to Keys.LANG_HINDI, Lang.EN to Keys.LANG_ENGLISH)
        options.forEachIndexed { index, option ->
            val button = buildLanguageButton(option.first, option.second)
            val params = LinearLayout.LayoutParams(0, context.dp(52), 1f)
            if (index == 0) params.rightMargin = context.dp(6) else params.leftMargin = context.dp(6)
            row.addView(button, params)
        }
        panel.addView(row, marginTop(12))

        // Larger text switch
        panel.addView(buildLargeTextRow(), marginTop(22))
        return panel
    }

    private fun buildLanguageButton(code: String, textKey: String): LinearLayout {
        val button = LinearLayout(context)
        button.orientation = LinearLayout.HORIZONTAL
        button.gravity = Gravity.CENTER_VERTICAL
        button.setPadding(context.dp(18), 0, context.dp(18), 0)
        Ui.makeFocusable(button)

        val name = Ui.text(context, 16f, Palette.TEXT)
        binder.bind(name, textKey)
        button.addView(name, LinearLayout.LayoutParams(0, WRAP, 1f))
        val check = Ui.icon(context, R.drawable.ic_check, Palette.TEXT)
        button.addView(check, LinearLayout.LayoutParams(context.dp(20), context.dp(20)))

        button.setOnClickListener {
            prefs.language = code
            Translator.language = code
            binder.refresh()              // texts in this pop-up
            updateLanguageButtons()
            listener.onLanguageChanged()  // texts on the home screen
        }
        languageButtons.add(code to button)
        languageChecks.add(check)
        return button
    }

    private fun updateLanguageButtons() {
        languageButtons.forEachIndexed { index, item ->
            val chosen = item.first == prefs.language
            val normal = if (chosen) {
                Ui.shape(context, Palette.CHOICE_SELECTED_FILL, 16, Palette.CHOICE_SELECTED_BORDER, 1)
            } else {
                Ui.shape(context, Color.TRANSPARENT, 16, Palette.CHOICE_BORDER, 1)
            }
            item.second.background = Ui.focusStates(
                normal,
                Ui.shape(context, Palette.CHOICE_SELECTED_FILL, 16, Palette.FOCUS_RING, 3),
            )
            languageChecks[index].visibility = if (chosen) View.VISIBLE else View.INVISIBLE
        }
    }

    /** Row with the text "Larger app labels" and an on/off switch. */
    private fun buildLargeTextRow(): View {
        val row = LinearLayout(context)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_VERTICAL
        row.setPadding(context.dp(20), 0, context.dp(20), 0)
        Ui.makeFocusable(row)
        row.background = Ui.focusStates(
            Ui.shape(context, Palette.TOGGLE_ROW, 16),
            Ui.shape(context, Palette.TOGGLE_ROW, 16, Palette.FOCUS_RING, 3),
        )

        val text = Ui.text(context, 16f, Palette.TEXT)
        binder.bind(text, Keys.LARGE_TEXT)
        row.addView(text, LinearLayout.LayoutParams(0, WRAP, 1f))

        // The switch: a pill with a white knob that sits left (off) or right (on).
        toggleTrack = LinearLayout(context)
        toggleTrack.orientation = LinearLayout.HORIZONTAL
        toggleTrack.setPadding(context.dp(4), context.dp(4), context.dp(4), context.dp(4))
        val knob = View(context)
        knob.background = Ui.circle(Color.WHITE)
        toggleTrack.addView(knob, LinearLayout.LayoutParams(context.dp(20), context.dp(20)))
        row.addView(toggleTrack, LinearLayout.LayoutParams(context.dp(48), context.dp(28)))

        row.minimumHeight = context.dp(60)
        row.setOnClickListener {
            prefs.largeText = !prefs.largeText
            updateToggle()
            listener.onLargeTextChanged()
        }
        return row
    }

    private fun updateToggle() {
        val on = prefs.largeText
        toggleTrack.gravity = if (on) Gravity.END or Gravity.CENTER_VERTICAL else Gravity.START or Gravity.CENTER_VERTICAL
        toggleTrack.background = Ui.shape(context, if (on) Palette.THEME_SELECTED_BORDER else Palette.TOGGLE_OFF, 14)
    }

    // -------------------------------------------------------------- helpers

    /** Shows the panel of the active tab and styles the two tab buttons. */
    private fun updateTabs() {
        themePanel.visibility = if (showingThemeTab) View.VISIBLE else View.GONE
        generalPanel.visibility = if (showingThemeTab) View.GONE else View.VISIBLE
        styleTab(tabTheme, showingThemeTab)
        styleTab(tabGeneral, !showingThemeTab)
    }

    private fun styleTab(tab: TextView, active: Boolean) {
        val normal = Ui.shape(context, if (active) Color.WHITE else Color.TRANSPARENT, 12)
        val focused = Ui.shape(context, if (active) Color.WHITE else Palette.FOCUS_FILL, 12, Palette.FOCUS_RING, 2)
        tab.background = Ui.focusStates(normal, focused)
        tab.setTextColor(if (active) Palette.TEXT else Palette.TAB_TEXT)
    }

    private fun ringCircle(fill: Int) = Ui.circle(fill).also { it.setStroke(context.dp(2), Palette.FOCUS_RING) }

    private fun marginTop(dp: Int): LinearLayout.LayoutParams {
        val params = LinearLayout.LayoutParams(MATCH, WRAP)
        params.topMargin = context.dp(dp)
        return params
    }
}
