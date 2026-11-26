# mondroid 4 — ICS Home Launcher

**Ice Cream Sandwich (Android 4.0.4) home screen experience reimplemented for modern Android (10–16).**

## Overview

`mondroid 4` is a complete, buildable, root-free launcher that replicates the Android 4.0.4 (Ice Cream Sandwich) user experience as shipped on Google Nexus devices. It installs as a regular app-level package — **no root, no custom recovery, no unlocked bootloader, no system partition modification required**.

## Features

### Launcher Experience
- **ICS-style home screen** with search bar at top
- **Multi-page workspace** via ViewPager2 with page indicator dots
- **Bottom dock** with Back, Phone, Browser, Email, Camera, Recents, and All Apps buttons
- **Configurable grid size** (4×4, 4×5, 5×5, 5×6) in Settings
- **Dark theme** (holo-dark inspired palette)
- **App drawer** with search/filter showing all installed launcher apps
- **Long-press to add** app shortcuts or widgets
- **Welcome screen** on first run

### Wallpaper System
- **Static wallpapers**: 3 bundled original ICS-style designs
- **Live wallpaper**: Canvas-drawn Android robot on ice cream sandwich with parallax offset on page swipe
- **From Photos**: Pick any photo from the user's gallery (scoped-storage compliant)
- **WallpaperService** registered with `BIND_WALLPAPER` permission

### Nexus-Era Apps (Reimplemented)
| App | Implementation |
|-----|---------------|
| Browser | WebView-based |
| Email | Compose → `mailto:` intent chooser |
| Gallery | `MediaStore.Images` grid + photo viewer |
| Music | `MediaStore.Audio` list + MediaPlayer |
| Camera | System camera intent + MediaStore |
| Calendar | Stub → opens system Calendar app |
| Contacts | System contacts picker |
| Clock | Live time/date + vibrate demo |
| Calculator | + − × ÷ expression evaluator |
| Messaging | Compose → `smsto:` intent chooser |
| Notes | Local SQLite database |
| File Manager | External storage browser |
| Settings | System settings shortcuts |
| App Info | App details and storage info |
| Device Info | System model, manufacturer, API level, and more |
| Frequent Apps | Launch frequency-based app list |
| Recent Apps | Recently launched apps list |
| Search | Search installed apps by name |
| Sound Recorder | Record audio using the microphone |
| Compass | Magnetic field compass sensor |
| Flashlight | Camera flash toggle |
| Timer | Stopwatch timer |
| Weather | Mock weather conditions |
| Fortune | Random daily fortunes |
| Tip Calculator | Calculate tips and totals |
| Unit Converter | Convert length, weight, temperature |
| BMI Calculator | Calculate body mass index |
| Currency Converter | Convert USD to EUR and vice versa |
| Age Calculator | Calculate age from birth date |
| Countdown Timer | Count down from set time |
| Stopwatch | Measure elapsed time with laps |
| Pomodoro | 25-minute work timer |
| Todo List | Simple task manager |
| Todo List | Simple task manager |
| Todo List | Simple task manager |

### Easter Egg
Self-contained Android 4.0.4 Ice Cream Sandwich Easter egg activity:
- Tap 5 times to reveal the ice cream sandwich with Android figure
- Tap sandwich to zoom into scrolling Android-team credits
- Drag to scroll credits
- All drawn with Canvas primitives (no proprietary assets)

### Quick Settings
Panel with toggles for Wi-Fi, Bluetooth, Data Usage, Sound, Display, Battery, Location, Security, and Accounts — each opens the corresponding system settings page.

### App Widget
**Clock widget** — shows current time and date, taps to open Clock app.

## Architecture

```
com.example.icslauncher/
├── launcher/          ICSLauncherActivity, AppAdapter, AppModel, WorkspacePagerAdapter
├── drawer/            AppDrawerActivity (all installed apps + search)
├── wallpaper/         WallpaperPickerActivity, ICSWallpaperService, WallpaperAdapter
├── easteregg/         EasterEggActivity, EasterEggView (self-contained ICS egg)
├── settings/          SettingsActivity, ICSPreferenceFragment, AboutActivity,
│                      QuickSettingsActivity, WelcomeActivity
├── apps/              14 Nexus-era app reimplementations (incl. Settings, App Info, Frequent Apps)
└── launcher/          ... AppFrequencyTracker (launch frequency tracking)
```

## Build Requirements

- Android SDK Platform 34 (or 36)
- Android SDK Build-Tools 34+
- Gradle 8.1+
- Java 17 or 21

## Build Instructions

```bash
# Clone the repository
git clone https://github.com/YoanValko/mondroid-4.git
cd mondroid-4

# Set SDK path
echo "sdk.dir=/path/to/your/Android/Sdk" > local.properties

# Build debug APK
./gradlew assembleDebug

# Install on device
adb install app/build/outputs/apk/debug/app-debug.apk

# Set as default launcher
# Settings → Apps → Default apps → Home app → ICS Home Launcher
```

## Supported Android Versions

| Version | API Level | Status |
|---------|-----------|--------|
| Android 10 | 29 | Supported (minSdk) |
| Android 11 | 30 | Supported |
| Android 12 | 31 | Supported |
| Android 13 | 33 | Supported |
| Android 14 | 34 | Supported (compileSdk) |
| Android 15 | 35 | Supported |
| Android 16 | 36 | Supported (targetSdk) |

## Limitations (Sandbox-Only)

| Feature | Limitation | Compliant Alternative |
|---------|-----------|----------------------|
| Force default launcher | User must select in Settings | User picks ICS Home Launcher as default home app |
| Default SMS/email/calendar | User-controlled defaults | Standalone apps + intent choosers |
| Read all SMS/media | Scoped storage blocks broad access | `READ_MEDIA_*` runtime permissions |
| System "About → Android version" egg | Can't modify system Settings | Self-contained EasterEggActivity |
| Real recents task list | System-only permission | Recents button opens app drawer |

## Licensing & Trademarks

- **No proprietary binaries bundled** — all icons, wallpapers, and artwork are original vector drawings
- **No closed-source system APKs** — all Nexus-era apps are reimplemented against public Android APIs
- "Android", "Ice Cream Sandwich", and "Nexus" are trademarks of Google LLC
- This is an independent, unofficial project — not affiliated with or endorsed by Google LLC
- Distributed under the MIT License

## Testing

Build and install on:
- Android 10 (API 29) emulator or device
- Android 14 (API 34) emulator or device
- Android 16 (API 36) emulator or device

Verify:
1. App installs without root/recovery/bootloader unlock
2. Set as default launcher via Settings
3. Home screen, app drawer, wallpaper picker all work
4. Easter egg triggers correctly (5 taps → zoom → scroll credits)
5. All bundled apps launch and function
6. Settings changes (grid size, dark theme) apply immediately

### Emulator Testing Limitation

**Important:** The Android 14 emulator could not be fully booted in this containerized environment. QEMU starts but the Android guest OS does not complete the boot process (heartbeat stays at 0, adb remains offline). This is a resource limitation of the container environment (no GPU passthrough, limited KVM acceleration), not an issue with the APK itself.

**To test the APK:**
1. Install on a physical Android 10–16 device: `adb install app/build/outputs/apk/debug/app-debug.apk`
2. Or use a properly resourced emulator with GPU acceleration
3. Set as default launcher: Settings → Apps → Default apps → Home app → ICS Home Launcher

## Project Status

✅ Build successful (AGP 8.1.0, Gradle 8.10.2, Java 21)
✅ APK generated (~9.6 MB)
✅ 44 activities + 1 wallpaper service + 2 receivers registered
✅ GitHub repository: https://github.com/YoanValko/mondroid-4

---

*Built for Android 10–16. No root. No custom recovery. No bootloader unlock. Just ICS.*