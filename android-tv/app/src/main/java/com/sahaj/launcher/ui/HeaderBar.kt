package com.sahaj.launcher.ui

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.sahaj.launcher.R
import com.sahaj.launcher.i18n.Keys
import com.sahaj.launcher.i18n.TextBinder
import com.sahaj.launcher.i18n.Translator
import com.sahaj.launcher.util.TimeText

/**
 * The top strip of the home screen:
 *   [ internet status ]      [ time + date ]      [ settings ]
 */
class HeaderBar(
    private val context: Context,
    private val binder: TextBinder,
    onNetworkClick: () -> Unit,
    onSettingsClick: () -> Unit,
    private val onMoveDown: () -> Unit,
    private val onFocusChanged: () -> Unit = {},
) {
    /** The whole top strip - add this to the screen. */
    val view = FrameLayout(context)

    /** Buttons are public so the screen can move the remote's focus to them. */
    val networkButton: View
    val settingsButton: View

    private var online = true
    private val statusDot = Ui.circle(Palette.ONLINE_DOT)

    init {
        view.clipChildren = false
        view.setPadding(context.dp(48), context.dp(24), context.dp(48), 0)

        networkButton = buildNetworkButton(onNetworkClick)
        settingsButton = buildSettingsButton(onSettingsClick)

        view.addView(networkButton, FrameLayout.LayoutParams(WRAP, WRAP, Gravity.START or Gravity.TOP))
        view.addView(buildClock(), FrameLayout.LayoutParams(WRAP, WRAP, Gravity.CENTER_HORIZONTAL or Gravity.TOP))
        view.addView(settingsButton, FrameLayout.LayoutParams(WRAP, WRAP, Gravity.END or Gravity.TOP))
    }

    /** Tell the bar whether the internet is available (changes text + dot colour). */
    fun setOnline(isOnline: Boolean) {
        online = isOnline
        statusDot.setColor(if (isOnline) Palette.ONLINE_DOT else Palette.OFFLINE_DOT)
        // The texts themselves are refreshed by the screen's TextBinder.
    }

    // ------------------------------------------------------------------ left

    private fun buildNetworkButton(onClick: () -> Unit): View {
        val button = LinearLayout(context)
        button.orientation = LinearLayout.HORIZONTAL
        button.gravity = Gravity.CENTER_VERTICAL
        button.setPadding(context.dp(8), context.dp(6), context.dp(16), context.dp(6))
        Ui.makeFocusable(button)
        button.background = Ui.focusStates(
            Ui.shape(context, Color.TRANSPARENT, 16),
            Ui.shape(context, Palette.FOCUS_FILL, 16, Palette.FOCUS_RING, 2),
        )
        button.setOnClickListener { onClick() }
        button.setOnFocusChangeListener { _, _ -> onFocusChanged() }
        moveDownOnDpadDown(button)

        // Round wifi icon
        val iconCircle = FrameLayout(context)
        iconCircle.background = Ui.circle(Palette.ICON_CIRCLE)
        iconCircle.addView(
            Ui.icon(context, R.drawable.ic_wifi, Palette.ICON_GREEN),
            FrameLayout.LayoutParams(context.dp(22), context.dp(22), Gravity.CENTER),
        )
        button.addView(iconCircle, LinearLayout.LayoutParams(context.dp(36), context.dp(36)))

        // Two lines of text: "Internet connected" + dot "All looking good"
        val texts = LinearLayout(context)
        texts.orientation = LinearLayout.VERTICAL
        val title = Ui.text(context, 14f, Palette.TEXT, medium = true)
        binder.bind(title) { Translator.t(if (online) Keys.NET_CONNECTED else Keys.NET_OFFLINE) }
        texts.addView(title, LinearLayout.LayoutParams(WRAP, WRAP))

        val subRow = LinearLayout(context)
        subRow.orientation = LinearLayout.HORIZONTAL
        subRow.gravity = Gravity.CENTER_VERTICAL
        val dot = View(context)
        dot.background = statusDot
        subRow.addView(dot, LinearLayout.LayoutParams(context.dp(6), context.dp(6)))
        val sub = Ui.text(context, 11f, Palette.TEXT_SOFT)
        binder.bind(sub) { Translator.t(if (online) Keys.NET_OK else Keys.NET_OFFLINE_HINT) }
        val subParams = LinearLayout.LayoutParams(WRAP, WRAP)
        subParams.leftMargin = context.dp(6)
        subRow.addView(sub, subParams)
        val subRowParams = LinearLayout.LayoutParams(WRAP, WRAP)
        subRowParams.topMargin = context.dp(2)
        texts.addView(subRow, subRowParams)

        val textsParams = LinearLayout.LayoutParams(WRAP, WRAP)
        textsParams.leftMargin = context.dp(10)
        button.addView(texts, textsParams)
        return button
    }

    // ---------------------------------------------------------------- centre

    private fun buildClock(): View {
        val box = LinearLayout(context)
        box.orientation = LinearLayout.VERTICAL
        box.gravity = Gravity.CENTER_HORIZONTAL

        val row = LinearLayout(context)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_HORIZONTAL

        val time = Ui.text(context, 52f, Palette.TEXT)
        time.typeface = android.graphics.Typeface.create("sans-serif-light", android.graphics.Typeface.NORMAL)
        time.includeFontPadding = false
        binder.bind(time) { TimeText.clock() }
        row.addView(time, LinearLayout.LayoutParams(WRAP, WRAP))

        val period = Ui.text(context, 16f, Palette.CLOCK_PERIOD)
        binder.bind(period) { TimeText.period() }
        val periodParams = LinearLayout.LayoutParams(WRAP, WRAP)
        periodParams.leftMargin = context.dp(8)
        row.addView(period, periodParams)
        box.addView(row, LinearLayout.LayoutParams(WRAP, WRAP))

        val date: TextView = Ui.text(context, 14f, Palette.TEXT_SOFT)
        binder.bind(date) { TimeText.date() }
        val dateParams = LinearLayout.LayoutParams(WRAP, WRAP)
        dateParams.topMargin = context.dp(4)
        box.addView(date, dateParams)
        return box
    }

    // ----------------------------------------------------------------- right

    private fun buildSettingsButton(onClick: () -> Unit): View {
        val button = LinearLayout(context)
        button.orientation = LinearLayout.HORIZONTAL
        button.gravity = Gravity.CENTER_VERTICAL
        button.setPadding(context.dp(16), 0, context.dp(20), 0)
        button.minimumHeight = context.dp(44)
        Ui.makeFocusable(button)
        button.background = Ui.focusStates(
            Ui.shape(context, Color.TRANSPARENT, 24, Palette.OUTLINE, 1),
            Ui.shape(context, Palette.FOCUS_FILL, 24, Palette.FOCUS_RING, 2),
        )
        button.setOnClickListener { onClick() }
        button.setOnFocusChangeListener { _, _ -> onFocusChanged() }
        moveDownOnDpadDown(button)

        button.addView(
            Ui.icon(context, R.drawable.ic_settings, Palette.TEXT),
            LinearLayout.LayoutParams(context.dp(20), context.dp(20)),
        )
        val label = Ui.text(context, 14f, Palette.TEXT, medium = true)
        binder.bind(label, Keys.SETTINGS)
        val labelParams = LinearLayout.LayoutParams(WRAP, WRAP)
        labelParams.leftMargin = context.dp(8)
        button.addView(label, labelParams)
        return button
    }

    /** Pressing DOWN on a top button sends the remote's focus back to the app tiles. */
    private fun moveDownOnDpadDown(button: View) {
        button.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                onMoveDown()
                true
            } else {
                false
            }
        }
    }
}
