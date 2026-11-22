package com.sahaj.launcher.i18n

/*
 * ============================================================================
 *  TEXT MAPPING FILE  -  THE ONLY FILE YOU NEED TO EDIT TO CHANGE WORDING
 * ============================================================================
 *
 *  Each block below is one text on the screen:
 *
 *      Keys.SOME_NAME to mapOf(
 *          HI to "हिन्दी text (shown when language = Hindi)",
 *          EN to "English text (shown when language = English)",
 *      ),
 *
 *  - Hindi is the default language. English is the mapped translation.
 *  - To correct a text: change only what is inside the quotes "...".
 *  - Keep %s exactly as it is where you see it (the app fills in a name there).
 *  - Do not remove the commas at the end of lines.
 *
 *  ADDING A NEW LANGUAGE LATER (for example Tamil):
 *    1. In Translator.kt add:  const val TA = "ta"  and add it to Lang.all
 *    2. Here, add one extra line in every block:   TA to "....",
 *    3. In SettingsDialog.kt add one more language button.
 *    If a language line is missing, the app safely shows Hindi instead.
 */
object TextMap {

    private const val HI = Lang.HI
    private const val EN = Lang.EN

    val entries: Map<String, Map<String, String>> = mapOf(

        // ===================== TOP BAR =====================
        Keys.NET_CONNECTED to mapOf(
            HI to "इंटरनेट से जुड़े हैं",
            EN to "Internet connected",
        ),
        Keys.NET_OK to mapOf(
            HI to "सब ठीक है",
            EN to "All looking good",
        ),
        Keys.NET_OFFLINE to mapOf(
            HI to "इंटरनेट नहीं जुड़ा",
            EN to "Not connected",
        ),
        Keys.NET_OFFLINE_HINT to mapOf(
            HI to "वाई-फ़ाई जाँचें",
            EN to "Please check Wi-Fi",
        ),
        Keys.SETTINGS to mapOf(
            HI to "सेटिंग्स",
            EN to "Settings",
        ),

        // ===================== CLOCK LABEL =====================
        // Hindi shows the part of the day. English shows AM / PM.
        Keys.PERIOD_MORNING to mapOf(
            HI to "सुबह",
            EN to "AM",
        ),
        Keys.PERIOD_AFTERNOON to mapOf(
            HI to "दोपहर",
            EN to "PM",
        ),
        Keys.PERIOD_EVENING to mapOf(
            HI to "शाम",
            EN to "PM",
        ),
        Keys.PERIOD_NIGHT to mapOf(
            HI to "रात",
            EN to "PM",
        ),

        // ===================== APP TILES =====================
        Keys.APP_YOUTUBE_NAME to mapOf(
            HI to "यूट्यूब",
            EN to "YouTube",
        ),
        Keys.APP_YOUTUBE_DESC to mapOf(
            HI to "अपनी पसंद के वीडियो देखें",
            EN to "Watch something you love",
        ),
        Keys.APP_NETFLIX_NAME to mapOf(
            HI to "नेटफ्लिक्स",
            EN to "Netflix",
        ),
        Keys.APP_NETFLIX_DESC to mapOf(
            HI to "फ़िल्मों और कहानियों की दुनिया",
            EN to "A world of films and stories",
        ),
        Keys.APP_LIVETV_NAME to mapOf(
            HI to "लाइव टीवी",
            EN to "Live TV",
        ),
        Keys.APP_LIVETV_DESC to mapOf(
            HI to "आपके पसंदीदा चैनल, एक जगह",
            EN to "Your favourite channels, all together",
        ),
        Keys.APP_JIOHOTSTAR_NAME to mapOf(
            HI to "जियोहॉटस्टार",
            EN to "JioHotstar",
        ),
        Keys.APP_JIOHOTSTAR_DESC to mapOf(
            HI to "खेल, फ़िल्में और बहुत कुछ",
            EN to "Sports, movies, and a little more",
        ),
        Keys.APP_MUSIC_NAME to mapOf(
            HI to "संगीत",
            EN to "Music",
        ),
        Keys.APP_MUSIC_DESC to mapOf(
            HI to "हर पल के लिए एक धुन",
            EN to "A melody for every moment",
        ),

        // ===================== POP-UPS =====================
        Keys.NOT_INSTALLED_BODY to mapOf(
            HI to "यह ऐप इस टीवी पर इंस्टॉल नहीं है। पहले इसे प्ले स्टोर से इंस्टॉल करें।",
            EN to "This app is not installed on this TV. Please install it from the Play Store first.",
        ),
        Keys.NET_DIALOG_TITLE to mapOf(
            HI to "इंटरनेट की जानकारी",
            EN to "Network information",
        ),
        Keys.NET_DIALOG_BODY to mapOf(
            HI to "इस टीवी पर वाई-फ़ाई सेटिंग्स यहाँ से नहीं खुल सकीं। कृपया टीवी की अपनी सेटिंग्स में जाकर नेटवर्क देखें।",
            EN to "Wi-Fi settings could not be opened from here. Please open your TV's own Settings to check the network.",
        ),
        Keys.BACK_HOME to mapOf(
            HI to "होम पर वापस जाएँ",
            EN to "Back to home",
        ),

        // ===================== SETTINGS POP-UP =====================
        Keys.SETTINGS_TITLE to mapOf(
            HI to "अपने हिसाब से बनाएँ",
            EN to "Make yourself at home",
        ),
        Keys.TAB_THEME to mapOf(
            HI to "रंग और थीम",
            EN to "Look & feel",
        ),
        Keys.TAB_GENERAL to mapOf(
            HI to "भाषा और अक्षर",
            EN to "Language & text",
        ),
        Keys.THEME_HINT to mapOf(
            HI to "जो रंग आँखों को भाए, उसे चुनें।",
            EN to "Choose the colours that feel right for you.",
        ),
        // %s is replaced by the name of the chosen theme - keep it.
        Keys.THEME_CURRENT to mapOf(
            HI to "चुनी हुई थीम: %s",
            EN to "Current theme: %s",
        ),
        Keys.THEME_IVORY to mapOf(
            HI to "हल्का क्रीम",
            EN to "Warm Ivory",
        ),
        Keys.THEME_SAGE to mapOf(
            HI to "शांत हरा",
            EN to "Garden Sage",
        ),
        Keys.THEME_SKY to mapOf(
            HI to "खुला आसमान",
            EN to "Quiet Sky",
        ),
        Keys.THEME_SAND to mapOf(
            HI to "नरम सुनहरा",
            EN to "Soft Sand",
        ),
        // ---- "Choose apps" tab ----
        Keys.TAB_APPS to mapOf(
            HI to "ऐप्स चुनें",
            EN to "Choose apps",
        ),
        Keys.APPS_HINT to mapOf(
            HI to "होम स्क्रीन पर दिखाने वाले ऐप चुनें।",
            EN to "Choose the apps to show on the home screen.",
        ),
        // The two %d are replaced by numbers (chosen, maximum) - keep both, in this order.
        Keys.APPS_COUNT to mapOf(
            HI to "चुने गए ऐप: %d / %d",
            EN to "Apps chosen: %d / %d",
        ),
        Keys.APPS_RESET to mapOf(
            HI to "शुरुआती ऐप वापस लाएँ",
            EN to "Restore default apps",
        ),
        Keys.APPS_NOT_INSTALLED to mapOf(
            HI to "इंस्टॉल नहीं है",
            EN to "not installed",
        ),
        Keys.APP_GENERIC_DESC to mapOf(
            HI to "इस ऐप को खोलें",
            EN to "Open this app",
        ),

        Keys.LANGUAGE_LABEL to mapOf(
            HI to "भाषा / Language",
            EN to "Language / भाषा",
        ),
        // Language names are written in their own language on purpose.
        Keys.LANG_HINDI to mapOf(
            HI to "हिन्दी",
            EN to "हिन्दी",
        ),
        Keys.LANG_ENGLISH to mapOf(
            HI to "English",
            EN to "English",
        ),
        Keys.LARGE_TEXT to mapOf(
            HI to "बड़े अक्षर",
            EN to "Larger app labels",
        ),
        Keys.PREFS_SAVED to mapOf(
            HI to "आपकी पसंद इस डिवाइस पर सहेजी जाती है।",
            EN to "Your preferences are saved on this device.",
        ),
    )
}
