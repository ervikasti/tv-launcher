package com.sahaj.launcher.ui

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.sahaj.launcher.config.Logo

/** Builds the logo picture/text that sits inside an app tile (and in pop-ups). */
object LogoView {

    fun create(context: Context, logo: Logo): LinearLayout {
        val box = LinearLayout(context)
        box.orientation = LinearLayout.VERTICAL
        box.gravity = Gravity.CENTER

        // Picture part (if any)
        logo.drawableRes?.let { res ->
            val image = ImageView(context)
            image.setImageResource(res)
            box.addView(image, LinearLayout.LayoutParams(WRAP, WRAP))
        }

        // The app's own icon (for apps the viewer picked in Settings)
        logo.drawable?.let { icon ->
            val image = ImageView(context)
            image.setImageDrawable(icon)
            image.scaleType = ImageView.ScaleType.FIT_CENTER
            box.addView(image, LinearLayout.LayoutParams(context.dp(56), context.dp(56)))
        }

        // Text part (if any), e.g. the big "N" of Netflix
        logo.wordmark?.let { word ->
            val text = TextView(context)
            text.text = word
            text.setTextColor(logo.wordmarkColor)
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, logo.wordmarkSizeSp)
            text.typeface = Typeface.create(logo.wordmarkFont, Typeface.NORMAL)
            text.includeFontPadding = false
            box.addView(text, LinearLayout.LayoutParams(WRAP, WRAP))
        }
        return box
    }
}
