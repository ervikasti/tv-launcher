package com.sahaj.launcher.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.sahaj.launcher.R
import com.sahaj.launcher.config.AppEntry
import com.sahaj.launcher.config.LauncherTheme
import com.sahaj.launcher.i18n.Keys
import com.sahaj.launcher.i18n.Translator

/**
 * A small information pop-up with a title, a message and a "Back to home" button.
 * Used when an app is not installed and when Wi-Fi settings cannot be opened.
 * Pass [entry] to also show that app's coloured logo block.
 */
class MessageDialog(
    context: Context,
    theme: LauncherTheme,
    title: String,
    message: String,
    entry: AppEntry? = null,
) {
    val dialog = Dialog(context, R.style.Theme_Sahaj_Dialog)

    init {
        val root = FrameLayout(context)
        root.setOnClickListener { dialog.dismiss() }

        val card = LinearLayout(context)
        card.orientation = LinearLayout.VERTICAL
        card.isClickable = true
        card.setPadding(context.dp(28), context.dp(24), context.dp(28), context.dp(24))
        card.background = Ui.shape(context, theme.surface, 28)

        // Title
        val titleView = Ui.text(context, 22f, Palette.TEXT, medium = true)
        titleView.text = title
        card.addView(titleView, LinearLayout.LayoutParams(MATCH, WRAP))

        // Coloured block with the app logo and its description
        if (entry != null) {
            val block = LinearLayout(context)
            block.orientation = LinearLayout.VERTICAL
            block.gravity = Gravity.CENTER
            block.setPadding(context.dp(20), context.dp(20), context.dp(20), context.dp(20))
            block.background = Ui.shape(context, entry.tileColor, 16)
            block.addView(LogoView.create(context, entry.logo), LinearLayout.LayoutParams(WRAP, WRAP))
            val desc = Ui.text(context, 15f, entry.inkColor, medium = true)
            desc.text = Translator.t(entry.descKey)
            desc.gravity = Gravity.CENTER
            val descParams = LinearLayout.LayoutParams(WRAP, WRAP)
            descParams.topMargin = context.dp(10)
            block.addView(desc, descParams)
            val blockParams = LinearLayout.LayoutParams(MATCH, WRAP)
            blockParams.topMargin = context.dp(18)
            card.addView(block, blockParams)
        }

        // Message
        val body = Ui.text(context, 15f, Palette.TEXT_MUTED)
        body.text = message
        val bodyParams = LinearLayout.LayoutParams(MATCH, WRAP)
        bodyParams.topMargin = context.dp(18)
        card.addView(body, bodyParams)

        // "Back to home" button
        val button = LinearLayout(context)
        button.orientation = LinearLayout.HORIZONTAL
        button.gravity = Gravity.CENTER
        Ui.makeFocusable(button)
        button.background = Ui.focusStates(
            Ui.shape(context, Palette.BADGE, 12),
            Ui.shape(context, Palette.BADGE, 12, Palette.TILE_FOCUS_RING, 4),
        )
        button.addView(
            Ui.icon(context, R.drawable.ic_home, Color.WHITE),
            LinearLayout.LayoutParams(context.dp(20), context.dp(20)),
        )
        val buttonText = Ui.text(context, 15f, Color.WHITE, medium = true)
        buttonText.text = Translator.t(Keys.BACK_HOME)
        val buttonTextParams = LinearLayout.LayoutParams(WRAP, WRAP)
        buttonTextParams.leftMargin = context.dp(10)
        button.addView(buttonText, buttonTextParams)
        button.setOnClickListener { dialog.dismiss() }
        val buttonParams = LinearLayout.LayoutParams(MATCH, context.dp(48))
        buttonParams.topMargin = context.dp(22)
        card.addView(button, buttonParams)

        root.addView(card, FrameLayout.LayoutParams(context.dp(520), WRAP, Gravity.CENTER))
        dialog.setContentView(root)
        dialog.window?.setLayout(MATCH, MATCH)
        dialog.setOnShowListener { button.requestFocus() }
    }

    fun show() = dialog.show()
}
