package com.sahaj.launcher.config

import android.graphics.Color
import com.sahaj.launcher.R
import com.sahaj.launcher.i18n.Keys

/**
 * How the logo inside a tile is drawn.
 *  - drawableRes : a picture from res/drawable (optional)
 *  - wordmark    : plain text drawn as logo, e.g. the big "N" of Netflix (optional)
 */
data class Logo(
    val drawableRes: Int? = null,
    val wordmark: String? = null,
    val wordmarkColor: Int = Color.BLACK,
    val wordmarkSizeSp: Float = 16f,
    val wordmarkFont: String = "sans-serif-medium",
)

/**
 * One app tile on the home screen.
 *  - nameKey / descKey : text keys (wording is in TextMap.kt)
 *  - packages          : Android package names to try, in order. The first one
 *                        installed on the TV is opened.
 *  - tileColor/inkColor: tile background and logo/label colour
 */
data class AppEntry(
    val id: String,
    val nameKey: String,
    val descKey: String,
    val packages: List<String>,
    val tileColor: Int,
    val inkColor: Int,
    val logo: Logo,
)

/**
 * THE LIST OF TILES ON THE HOME SCREEN (left to right).
 *
 * To reorder: move a block up/down.   To remove: delete a block.
 * To fix an app that does not open: correct its package name in `packages`.
 *   (Find a package name on your TV with:  adb shell pm list packages | grep netflix)
 * Package names below are the usual Android TV ones - please verify on your TV.
 */
object AppCatalog {
    val apps: List<AppEntry> = listOf(
        AppEntry(
            id = "youtube",
            nameKey = Keys.APP_YOUTUBE_NAME,
            descKey = Keys.APP_YOUTUBE_DESC,
            packages = listOf("com.google.android.youtube.tv"),
            tileColor = Color.parseColor("#faeeeb"),
            inkColor = Color.parseColor("#b4372b"),
            logo = Logo(drawableRes = R.drawable.logo_youtube),
        ),
        AppEntry(
            id = "netflix",
            nameKey = Keys.APP_NETFLIX_NAME,
            descKey = Keys.APP_NETFLIX_DESC,
            packages = listOf("com.netflix.ninja"),
            tileColor = Color.parseColor("#eee9e3"),
            inkColor = Color.parseColor("#bd2636"),
            logo = Logo(
                wordmark = "N",
                wordmarkColor = Color.parseColor("#bd2636"),
                wordmarkSizeSp = 52f,
                wordmarkFont = "sans-serif-black",
            ),
        ),
        AppEntry(
            id = "livetv",
            nameKey = Keys.APP_LIVETV_NAME,
            descKey = Keys.APP_LIVETV_DESC,
            // Live TV differs per TV brand. Add your TV's live-TV package here if it does not open.
            packages = listOf("com.google.android.tv", "com.mitv.livetv", "com.sony.dtv.tvx"),
            tileColor = Color.parseColor("#e0e8d9"),
            inkColor = Color.parseColor("#496145"),
            logo = Logo(drawableRes = R.drawable.logo_livetv),
        ),
        AppEntry(
            id = "jiohotstar",
            nameKey = Keys.APP_JIOHOTSTAR_NAME,
            descKey = Keys.APP_JIOHOTSTAR_DESC,
            packages = listOf("in.startv.hotstaronly", "in.startv.hotstartvonly", "in.startv.hotstar"),
            tileColor = Color.parseColor("#e7e7f2"),
            inkColor = Color.parseColor("#474a7b"),
            logo = Logo(
                drawableRes = R.drawable.logo_star,
                wordmark = "JioHotstar",
                wordmarkColor = Color.parseColor("#424774"),
                wordmarkSizeSp = 15f,
                wordmarkFont = "sans-serif-medium",
            ),
        ),
        AppEntry(
            id = "music",
            nameKey = Keys.APP_MUSIC_NAME,
            descKey = Keys.APP_MUSIC_DESC,
            packages = listOf("com.google.android.youtube.tvmusic", "com.spotify.tv.android"),
            tileColor = Color.parseColor("#f1e5d6"),
            inkColor = Color.parseColor("#87613e"),
            logo = Logo(drawableRes = R.drawable.logo_music),
        ),
    )
}
