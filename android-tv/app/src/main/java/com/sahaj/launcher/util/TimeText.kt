package com.sahaj.launcher.util

import com.sahaj.launcher.i18n.Keys
import com.sahaj.launcher.i18n.Lang
import com.sahaj.launcher.i18n.Translator
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** Builds the clock texts shown at the top centre of the screen. */
object TimeText {

    /** Time like "7:05" (12-hour, no AM/PM - that comes from [period]). */
    fun clock(): String = SimpleDateFormat("h:mm", Locale.ENGLISH).format(Date())

    /** Small label next to the time: Hindi = सुबह/दोपहर/शाम/रात, English = AM/PM (see TextMap.kt). */
    fun period(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val key = when {
            hour < 12 -> Keys.PERIOD_MORNING
            hour < 17 -> Keys.PERIOD_AFTERNOON
            hour < 20 -> Keys.PERIOD_EVENING
            else -> Keys.PERIOD_NIGHT
        }
        return Translator.t(key)
    }

    /** Date like "रविवार, 22 नवंबर" (Hindi) or "Sunday, 22 November" (English). */
    fun date(): String {
        val locale = if (Translator.language == Lang.HI) Locale("hi", "IN") else Locale("en", "IN")
        return SimpleDateFormat("EEEE, d MMMM", locale).format(Date())
    }

    /** Milliseconds until the next minute starts - used to refresh the clock exactly on time. */
    fun millisToNextMinute(): Long = 60_000L - (System.currentTimeMillis() % 60_000L) + 100L
}
