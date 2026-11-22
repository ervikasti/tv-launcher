# Sahaj TV Launcher (Android TV)

Native Kotlin home-screen launcher built from the Figma design. No extra libraries,
no heavy effects (soft colour clouds instead of blur), so it stays fast on low-end TVs.

## 1. Get the APK (free, no installs on your PC)

1. Copy the `android-tv` folder and the `.github` folder into your GitHub repository
   (same level as `src/`, `package.json`) and push.
2. On GitHub open **Actions → Build Android TV APK**. It starts by itself after a push
   (or press **Run workflow**). It takes about 5 minutes.
3. Open the finished run, scroll to **Artifacts**, download **sahaj-launcher-apk**, unzip.
   Use `app-release.apk` (use `app-debug.apk` only if the release one fails to install).

*Alternative:* open the `android-tv` folder in Android Studio and choose
**Build → Build APK(s)**.

## 2. Install on the TV

- **USB drive:** copy the APK, plug into the TV, open it with a file manager app
  (allow "Install unknown apps" when asked), or
- **ADB over Wi-Fi:** `adb connect <TV-IP>:5555` then `adb install app-release.apk`

## 3. Make it the home screen

Press the TV's Home button. If Android asks which launcher to use, choose **Sahaj Launcher → Always**.
If it does not ask, run once from a PC:

    adb shell cmd package set-home-activity com.sahaj.launcher/.MainActivity

Some TVs (especially Google TV) block replacing the system home screen. Then the app
still works: open it from the TV's **Apps** list.

## 4. How to change things (modular layout)

All code is in `app/src/main/java/com/sahaj/launcher/`

| Folder / file          | What it is for                                              |
|------------------------|-------------------------------------------------------------|
| `i18n/TextMap.kt`      | **Every on-screen text, Hindi ↔ English side by side. Edit wording here.** |
| `i18n/TextKeys.kt`     | Names of the texts (only touch when adding a new text)      |
| `i18n/Translator.kt`   | Picks the right language; also where new languages are registered |
| `config/AppCatalog.kt` | The app tiles: order, colours, logos, **package names**     |
| `config/Themes.kt`     | The 4 colour themes                                         |
| `config/LauncherConfig.kt` | Simple knobs: starting tile, animation on/off, sizes   |
| `ui/HomeScreen.kt`     | Main screen layout                                          |
| `ui/HeaderBar.kt`      | Top strip: internet status, clock, settings button          |
| `ui/AppTileView.kt`    | One app tile                                                |
| `ui/SettingsDialog.kt` | Settings pop-up (theme, language, large text)               |
| `ui/MessageDialog.kt`  | "App not installed" / info pop-up                           |
| `util/`                | Opening apps, internet check, clock text                    |
| `res/drawable/`        | Icons and app logos (vector files)                          |

Examples:
- **Fix a Hindi word:** open `TextMap.kt`, find the line, change the text inside quotes.
- **App does not open:** correct its package name in `AppCatalog.kt`
  (find it with `adb shell pm list packages | grep <name>`). Several names can be listed;
  the first installed one is used.
- **Very slow TV:** set `ANIMATION_MS = 0` in `LauncherConfig.kt`.

## Remote control

Left/Right move between tiles (wraps around), Up goes to Settings, Down comes back,
OK opens the app, Back does nothing on the home screen. The Home button returns here.
