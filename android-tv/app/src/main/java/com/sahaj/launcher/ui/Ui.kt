package com.sahaj.launcher.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

/** Shorthand for "fill the parent" / "wrap the content" in layouts. */
const val MATCH = ViewGroup.LayoutParams.MATCH_PARENT
const val WRAP = ViewGroup.LayoutParams.WRAP_CONTENT

/** Converts "dp" (TV-independent size) to real pixels. */
fun Context.dp(value: Int): Int = (value * resources.displayMetrics.density + 0.5f).toInt()

/** Same as [dp] but keeps decimals. */
fun Context.dpf(value: Float): Float = value * resources.displayMetrics.density

/** Small drawing / view helpers shared by all screens. */
object Ui {

    /** Rounded rectangle, optionally with a border. */
    fun shape(
        context: Context,
        fill: Int,
        radiusDp: Int,
        strokeColor: Int = Color.TRANSPARENT,
        strokeDp: Int = 0,
    ): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.setShape(GradientDrawable.RECTANGLE)
        drawable.setColor(fill)
        drawable.setCornerRadius(context.dpf(radiusDp.toFloat()))
        if (strokeDp > 0) drawable.setStroke(context.dp(strokeDp), strokeColor)
        return drawable
    }

    /** Filled circle. Size comes from the view it is used on. */
    fun circle(fill: Int): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.setShape(GradientDrawable.OVAL)
        drawable.setColor(fill)
        return drawable
    }

    /**
     * Soft round colour cloud (cheap replacement for a blur effect, easy on weak TVs).
     * [sizePx] is the width/height of the cloud.
     */
    fun glow(color: Int, sizePx: Int): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.setGradientType(GradientDrawable.RADIAL_GRADIENT)
        drawable.setGradientCenter(0.5f, 0.5f)
        drawable.setGradientRadius(sizePx / 2f)
        drawable.setColors(intArrayOf(withAlpha(color, 235), withAlpha(color, 0)))
        return drawable
    }

    /** Same colour with a different transparency (0-255). */
    fun withAlpha(color: Int, alpha: Int): Int =
        Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))

    /** Background that looks different when the remote's focus is on the view. */
    fun focusStates(normal: Drawable, focused: Drawable): StateListDrawable {
        val states = StateListDrawable()
        states.addState(intArrayOf(android.R.attr.state_focused), focused)
        states.addState(intArrayOf(), normal)
        return states
    }

    /** Makes a view selectable with the remote (D-pad) and pressable with OK. */
    fun makeFocusable(view: View) {
        view.isFocusable = true
        view.isClickable = true
        // We draw our own focus look, so switch off the system's default highlight.
        if (Build.VERSION.SDK_INT >= 26) view.setDefaultFocusHighlightEnabled(false)
    }

    /** One of our line icons (white in the file), recolored with [color]. */
    fun icon(context: Context, resId: Int, color: Int): ImageView {
        val image = ImageView(context)
        image.setImageResource(resId)
        image.setColorFilter(color)
        return image
    }

    /** Creates a TextView with the given size (sp), color and weight. */
    fun text(
        context: Context,
        sizeSp: Float,
        color: Int,
        medium: Boolean = false,
    ): TextView {
        val view = TextView(context)
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp)
        view.setTextColor(color)
        if (medium) view.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        return view
    }
}
