package com.sahaj.launcher

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.sahaj.launcher.config.AppEntry
import com.sahaj.launcher.config.Themes
import com.sahaj.launcher.data.Prefs
import com.sahaj.launcher.i18n.Keys
import com.sahaj.launcher.i18n.Translator
import com.sahaj.launcher.ui.HomeScreen
import com.sahaj.launcher.ui.MessageDialog
import com.sahaj.launcher.ui.SettingsDialog
import com.sahaj.launcher.util.AppLauncher
import com.sahaj.launcher.util.NetworkUtil
import com.sahaj.launcher.util.TimeText

/**
 * The only screen of the app. It connects the pieces together:
 *   Prefs (saved choices) -> HomeScreen (drawing) -> dialogs and app launching.
 * Look at HomeScreen.kt for the layout and AppCatalog.kt for the tile list.
 */
class MainActivity : Activity() {

    private lateinit var prefs: Prefs
    private lateinit var home: HomeScreen
    private var currentDialog: Dialog? = null
    private val handler = Handler(Looper.getMainLooper())

    /** Refreshes the clock exactly at the start of every minute. */
    private val clockTicker = object : Runnable {
        override fun run() {
            home.refreshClockAndNetwork()
            handler.postDelayed(this, TimeText.millisToNextMinute())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = Prefs(this)
        Translator.language = prefs.language

        home = HomeScreen(this, object : HomeScreen.Callbacks {
            override fun onOpenNetwork() = openNetwork()
            override fun onOpenSettings() = openSettings()
            override fun onLaunch(entry: AppEntry) = launchApp(entry)
        })
        home.applyTheme(Themes.byId(prefs.themeId))
        home.applyLargeText(prefs.largeText)
        setContentView(home.view)

        // Put the remote's cursor on the selected tile when the screen first appears.
        home.view.post { home.focusSelectedTile() }
    }

    override fun onResume() {
        super.onResume()
        handler.removeCallbacks(clockTicker)
        clockTicker.run()   // update now, then every minute
    }

    override fun onPause() {
        handler.removeCallbacks(clockTicker)
        super.onPause()
    }

    /** The HOME button was pressed while we are already open: close pop-ups, go back to tiles. */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        currentDialog?.dismiss()
        home.focusSelectedTile()
    }

    /** A home screen must not be closed with BACK, so BACK does nothing here. */
    @Suppress("OVERRIDE_DEPRECATION")
    override fun onBackPressed() {
        // intentionally empty
    }

    // ----------------------------------------------------------- actions

    private fun launchApp(entry: AppEntry) {
        if (AppLauncher.launch(this, entry)) return
        // App is not installed -> friendly message.
        show(
            MessageDialog(
                this,
                Themes.byId(prefs.themeId),
                Translator.t(entry.nameKey),
                Translator.t(Keys.NOT_INSTALLED_BODY),
                entry,
            ).dialog,
        )
    }

    private fun openNetwork() {
        if (NetworkUtil.openWifiSettings(this)) return
        show(
            MessageDialog(
                this,
                Themes.byId(prefs.themeId),
                Translator.t(Keys.NET_DIALOG_TITLE),
                Translator.t(Keys.NET_DIALOG_BODY),
            ).dialog,
        )
    }

    private fun openSettings() {
        val dialog = SettingsDialog(this, prefs, object : SettingsDialog.Listener {
            override fun onLanguageChanged() = home.onLanguageChanged()
            override fun onThemeChanged() = home.applyTheme(Themes.byId(prefs.themeId))
            override fun onLargeTextChanged() = home.applyLargeText(prefs.largeText)
        })
        show(dialog.dialog)
    }

    /** Shows a pop-up, closing any previous one. */
    private fun show(dialog: Dialog) {
        currentDialog?.dismiss()
        currentDialog = dialog
        dialog.show()
    }
}
