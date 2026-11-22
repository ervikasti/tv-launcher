package com.sahaj.launcher.util

import android.content.Context
import android.content.Intent
import com.sahaj.launcher.config.AppEntry

/** Opens other apps installed on the TV. */
object AppLauncher {

    /**
     * Tries each package name of [entry] in order and opens the first one that is installed.
     * Returns false if none of them is installed (the caller then shows a message).
     */
    fun launch(context: Context, entry: AppEntry): Boolean {
        val pm = context.packageManager
        for (pkg in entry.packages) {
            // TV apps usually have a special "leanback" start screen; fall back to the normal one.
            val intent = pm.getLeanbackLaunchIntentForPackage(pkg) ?: pm.getLaunchIntentForPackage(pkg)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                try {
                    context.startActivity(intent)
                    return true
                } catch (e: Exception) {
                    // This package could not start - try the next one.
                }
            }
        }
        return false
    }
}
