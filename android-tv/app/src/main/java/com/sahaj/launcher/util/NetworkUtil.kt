package com.sahaj.launcher.util

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.provider.Settings

/** Internet status check and a shortcut to the TV's Wi-Fi settings. */
object NetworkUtil {

    /** True when the TV currently has a working network connection (Wi-Fi or LAN cable). */
    @Suppress("DEPRECATION")
    fun isOnline(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        return try {
            manager.activeNetworkInfo?.isConnected == true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Opens the TV's Wi-Fi settings (or general settings if Wi-Fi page is missing).
     * Returns false if neither could be opened.
     */
    fun openWifiSettings(context: Context): Boolean {
        for (action in listOf(Settings.ACTION_WIFI_SETTINGS, Settings.ACTION_SETTINGS)) {
            try {
                context.startActivity(Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                return true
            } catch (e: Exception) {
                // Not available on this TV - try the next one.
            }
        }
        return false
    }
}
