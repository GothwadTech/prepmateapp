# Prepmate 🎓

<div align="center">

<img src="app/src/main/assets/icon_512.png" alt="Prepmate logo" width="128" />

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.2-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-FF6F00?style=for-the-badge)

**The official Prepmate Android app** — a native Jetpack Compose shell around the Prepmate web app
(`gtwdmate.pages.dev`) with offline caching, scoped device permissions, a Room database and
Firebase Cloud Messaging push notifications.

[Features](#-features) • [Tech Stack](#-tech-stack) • [Getting Started](#-getting-started) • [Configuration](#-configuration) • [Push Notifications](#-push-notifications-fcm) • [CI/CD & Releases](#-cicd--signing-secrets)

</div>

---

## 📱 Overview

Prepmate for Android loads the Prepmate web app inside a hardened WebView and adds the native
pieces a web app cannot provide on its own:

- a branded splash screen and Material 3 chrome that follows the web app's light/dark theme,
- offline-first loading with a retryable offline screen,
- camera / microphone / geolocation access scoped to the Prepmate origin only,
- a local Room database for offline drafts and a notification history,
- FCM push notifications and a `window.PrepmateApp` JavaScript bridge the web app can call.

---

## ✨ Features

- 🎨 **Material 3 & Edge-to-Edge**: Compose UI with dynamic light/dark theming that mirrors the web app's theme (class / `data-theme` observer with a luma fallback).
- ⚡ **Offline-First**: `LOAD_CACHE_ELSE_NETWORK` + ServiceWorker caching when offline, and an offline panel that overlays a still-mounted WebView so page state survives a network blip.
- 🔔 **Push Notifications**: Firebase Cloud Messaging with a dedicated `Prepmate Notifications` channel and a Prepmate notification icon — see [setup](#-push-notifications-fcm).
- 🧩 **JavaScript Bridge**: `window.PrepmateApp.postNotification()`, `.getPushToken()`, `.isDeviceOnline()`, `.saveOfflineDraft()`, `.showToast()`, `.setTheme()`.
- 🔒 **Scoped Permissions**: camera, microphone and geolocation are granted only to the Prepmate origin and requested lazily when the page needs them — no permission spam on first launch.
- 🔄 **MVVM State**: Kotlin Coroutines, `StateFlow` and `collectAsStateWithLifecycle`.
- 🛡️ **Automated CI/CD**: GitHub Actions build a signed Release APK, Debug APK and Play Store AAB on every push to `main`, and publish GitHub Releases on `v*` tags.

---

## 🛠 Tech Stack

| Layer | Technologies |
| :--- | :--- |
| **Language** | Kotlin 2.x |
| **UI** | Jetpack Compose (BOM), Material 3 |
| **Architecture** | MVVM + Repository pattern |
| **Local Storage** | Room Database (SQLite), SharedPreferences |
| **Networking** | Retrofit, OkHttp 4, Moshi (Kotlin codegen) |
| **Push** | Firebase Cloud Messaging (FCM) |
| **Build** | Gradle 9 (Kotlin DSL), Android Gradle Plugin 9 |
| **Testing** | JUnit 4, Robolectric, Roborazzi, AndroidX Test |

---

## 📂 Project Structure

```text
prepmateapp/
├── .github/
│   └── workflows/
│       ├── build.yml          # Build & sign APK / AAB on push to main (auto GitHub Release)
│       └── release.yml        # Build & publish to GitHub Releases on tag (v*)
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── assets/                       # icon_512.png (store / marketing icon)
│   │   │   ├── java/com/gothwad/prepmate/
│   │   │   │   ├── MainActivity.kt           # WebView screen, permissions, offline panel
│   │   │   │   ├── data/                     # PrepmateDatabase, PrepmateDao, PrepmateRepository
│   │   │   │   ├── ui/                       # PrepmateViewModel, PrepmateJavascriptInterface, theme/
│   │   │   │   └── utils/                    # PrepmateFirebaseMessagingService, PrepmateNotificationHelper
│   │   │   ├── res/                          # Launcher icons, splash, themes, notification icon
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                             # JVM + Robolectric tests
│   │   └── androidTest/                      # Instrumented tests
│   ├── build.gradle.kts                      # App module configuration & dependencies
│   └── proguard-rules.pro
├── gradle/
│   ├── libs.versions.toml                    # Version catalog
│   └── wrapper/                              # Gradle wrapper
├── gradle.properties                         # ⭐ Prepmate app configuration (app.* block)
├── .env.example                              # ⭐ TARGET_URL of the Prepmate web app (copy to .env)
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: Ladybug (2024.2.1+) or newer.
- **JDK**: Java 17 (Temurin recommended).
- **Android SDK**: API 35 (compileSdk & targetSdk), minimum API 23.

### Build locally

1. **Clone the repository:**
   ```bash
   git clone https://github.com/GothwadTech/prepmateapp.git
   cd prepmateapp
   ```

2. **Create the environment file:**
   ```bash
   cp .env.example .env
   ```

3. **Build the Debug APK:**
   ```bash
   chmod +x ./gradlew
   ./gradlew assembleDebug
   ```
   Output: `app/build/outputs/apk/debug/app-debug.apk`

4. **Run unit tests:**
   ```bash
   ./gradlew testDebugUnitTest
   ```

---

## ⚙️ Configuration

All app identity lives in one place so nothing is hardcoded in the Kotlin sources.

### `gradle.properties` — the `app.*` block

| Key | Value | Used for |
| :--- | :--- | :--- |
| `app.name` | `Prepmate` | Launcher label, notification fallback title, CI artifact names |
| `app.id` | `com.gothwad.prepmate` | `applicationId` (Play Store identity) |
| `app.versionCode` / `app.versionName` | `1` / `1.0.0` | Local builds only — CI passes `-PversionCode` / `-PversionName` |
| `app.jsBridgeName` | `PrepmateApp` | Name of the injected JS bridge (`window.PrepmateApp`) |
| `app.notificationChannelId` / `Name` / `Description` | `prepmate_notifications` … | Android notification channel |
| `app.prefsName` | `prepmate_prefs` | SharedPreferences file that caches the FCM token |

### `.env` — the web target

```bash
TARGET_URL=https://gtwdmate.pages.dev
```

`TARGET_URL` is the page the WebView loads **and** the only origin allowed to use the camera,
microphone and geolocation. `.env` is git-ignored; CI copies `.env.example` to `.env` automatically.

---

## 🔔 Push Notifications (FCM)

The FCM code ships in the app, but **push stays disabled until Firebase is wired up**:

1. Add your `google-services.json` to the `app/` directory.
2. Apply the Google Services plugin in `app/build.gradle.kts`:
   ```kotlin
   plugins {
     // ...
     alias(libs.plugins.google.services)
   }
   ```

Without them the app logs `Firebase is not configured ... Push notifications are DISABLED.` and
`window.PrepmateApp.getPushToken()` returns a locally generated placeholder token.

---

## 🔐 CI/CD & Signing Secrets

Two GitHub Actions workflows are included:

| Workflow | Trigger | What it does |
| :--- | :--- | :--- |
| `Prepmate - Build Signed APK & Play Store AAB` | push to `main` / manual | Builds, signs and uploads `Prepmate-v<version>-Release.apk`, `-Debug.apk`, `-PlayStore.aab` and creates a GitHub Release |
| `Prepmate - Release to GitHub Releases` | tag `v*` / manual | Same artifacts, published under the tag |

### Required GitHub Secrets

Add these under **Settings > Secrets and variables > Actions**:

| Secret Name | Description | Required |
| :--- | :--- | :---: |
| `RELEASE_KEYSTORE_BASE64` | Base64-encoded release `.jks` / `.keystore` | **Yes** |
| `KEYSTORE_PASSWORD` | Password of the release keystore | **Yes** |
| `KEY_ALIAS` | Key alias inside the keystore (auto-detected if omitted) | Optional |
| `KEY_PASSWORD` | Password of the key alias (defaults to `KEYSTORE_PASSWORD`) | Optional |

> If `RELEASE_KEYSTORE_BASE64` or `KEYSTORE_PASSWORD` is missing, the workflow aborts before building
> so an unsigned or wrongly signed build is never published.

### Generating `RELEASE_KEYSTORE_BASE64`

**Linux / macOS:**
```bash
base64 -i prepmate-release-key.jks | tr -d '\n' > keystore_base64.txt
```

**Windows (PowerShell):**
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("prepmate-release-key.jks")) | Set-Content keystore_base64.txt
```

Paste the contents of `keystore_base64.txt` into the `RELEASE_KEYSTORE_BASE64` secret.

---

## 🏷️ Triggering a Release

```bash
git tag v1.0.3
git push origin v1.0.3
```

The release workflow validates the signing secrets, builds the signed Release APK, Debug APK and
Play Store AAB, and publishes **Prepmate v1.0.3** on GitHub Releases with generated release notes.

---

## 🤝 Contributing

1. Create a feature branch (`git checkout -b feature/my-change`)
2. Commit your changes (`git commit -m 'Describe the change'`)
3. Push the branch (`git push origin feature/my-change`)
4. Open a Pull Request against `main`

---

<div align="center">

Made with ❤️ by **Gothwad Tech** for Prepmate learners.

</div>
