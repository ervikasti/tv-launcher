package com.sahaj.launcher.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.sahaj.launcher.R
import com.sahaj.launcher.config.AppEntry
import com.sahaj.launcher.config.LauncherConfig
import com.sahaj.launcher.config.displayName
import com.sahaj.launcher.i18n.TextBinder

/**
 * One app tile: coloured card with logo + app name.
 *
 * Two looks:
 *  - "selected" (the app shown in the big title): slightly bigger, green border,
 *    tick badge in the corner and a round "press OK" button underneath.
 *  - "focused" (remote's cursor is on it): thick ring around the tile.
 */
class AppTileView(
    context: Context,
    val entry: AppEntry,
    binder: TextBinder,
) : FrameLayout(context) {

    private val label = TextView(context)
    private val badge = FrameLayout(context)
    private val pointer = FrameLayout(context)
    private val pointerBackground = Ui.circle(Color.parseColor("#405b36"))
    private var selectedLook = false

    init {
        clipChildren = false   // lets the "press OK" button draw below the tile
        clipToPadding = false
        Ui.makeFocusable(this)

        // --- Logo + name, stacked in the middle ---
        val column = LinearLayout(context)
        column.orientation = LinearLayout.VERTICAL
        column.gravity = Gravity.CENTER
        column.addView(
            LogoView.create(context, entry.logo),
            LinearLayout.LayoutParams(MATCH, context.dp(LauncherConfig.LOGO_BOX_HEIGHT_DP)),
        )
        label.setTextColor(entry.inkColor)
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, NORMAL_LABEL_SP)
        label.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        label.gravity = Gravity.CENTER
        label.maxLines = 1
        label.ellipsize = TextUtils.TruncateAt.END
        label.setPadding(context.dp(8), 0, context.dp(8), 0)
        val labelParams = LinearLayout.LayoutParams(MATCH, WRAP)
        labelParams.topMargin = context.dp(8)
        column.addView(label, labelParams)
        addView(column, FrameLayout.LayoutParams(MATCH, MATCH))
        binder.bind(label) { entry.displayName() }   // name follows the chosen language

        // --- Tick badge (top-right corner) ---
        badge.background = Ui.circle(Palette.BADGE)
        badge.addView(
            Ui.icon(context, R.drawable.ic_check, Color.WHITE),
            FrameLayout.LayoutParams(context.dp(16), context.dp(16), Gravity.CENTER),
        )
        val badgeParams = FrameLayout.LayoutParams(context.dp(24), context.dp(24), Gravity.TOP or Gravity.END)
        badgeParams.topMargin = context.dp(8)
        badgeParams.rightMargin = context.dp(8)
        badge.visibility = View.GONE
        addView(badge, badgeParams)

        // --- Round "press OK" button below the tile ---
        pointer.background = pointerBackground
        pointer.addView(
            Ui.icon(context, R.drawable.ic_pointer, Color.WHITE),
            FrameLayout.LayoutParams(context.dp(30), context.dp(30), Gravity.CENTER),
        )
        val pointerParams = FrameLayout.LayoutParams(
            context.dp(48), context.dp(48), Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
        )
        pointerParams.bottomMargin = -context.dp(60)
        pointer.visibility = View.GONE
        addView(pointer, pointerParams)

        updateBackground()
    }

    /** Turns the "selected" look on/off (grow, border, tick, OK button). */
    fun setSelectedLook(selected: Boolean) {
        if (selectedLook == selected) return
        selectedLook = selected
        updateBackground()
        badge.visibility = if (selected) View.VISIBLE else View.GONE
        pointer.visibility = if (selected) View.VISIBLE else View.GONE
        elevation = if (selected) context.dpf(8f) else 0f

        val targetScaleX = if (selected) 1.05f else 1f
        val targetScaleY = if (selected) 1.10f else 1f
        val targetShift = if (selected) -context.dpf(4f) else 0f
        if (LauncherConfig.ANIMATION_MS > 0) {
            animate()
                .scaleX(targetScaleX)
                .scaleY(targetScaleY)
                .translationY(targetShift)
                .setDuration(LauncherConfig.ANIMATION_MS)
                .start()
        } else {
            scaleX = targetScaleX
            scaleY = targetScaleY
            translationY = targetShift
        }
    }

    /** Bigger (or normal) app name under the logo. */
    fun setLargeText(large: Boolean) {
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (large) LARGE_LABEL_SP else NORMAL_LABEL_SP)
    }

    /** Colour of the round "press OK" button (follows the chosen theme). */
    fun setPointerColor(color: Int) {
        pointerBackground.setColor(color)
        pointer.invalidate()
    }

    /** Draws the tile background for the current look (normal border vs. focus ring). */
    private fun updateBackground() {
        val border = if (selectedLook) Palette.TILE_SELECTED_BORDER else Color.TRANSPARENT
        val normal = Ui.shape(context, entry.tileColor, 20, border, 2)
        val focused = Ui.shape(context, entry.tileColor, 20, Palette.TILE_FOCUS_RING, 4)
        background = Ui.focusStates(normal, focused)
    }

    private companion object {
        const val NORMAL_LABEL_SP = 14f
        const val LARGE_LABEL_SP = 18f
    }
}
