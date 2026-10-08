# eSign Android SDK v2: Sample App

A minimal app integrating the Surepass eSign SDK (`io.surepass.sdk:esign-android-sdk-v2`).

## Requirements

AGP 8.6, Gradle 8.7, Kotlin 1.9.25, Java 17 (`jvmTarget` 17), `compileSdk` 35, `minSdk` 28.

## Setup

**settings.gradle**: the SDK is on GitHub Packages. Put a GitHub token with `read:packages` in
`~/.gradle/gradle.properties` as `gpr.user` / `gpr.key`.

```groovy
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = "https://maven.pkg.github.com/surepassio/esign-sample-app"
            credentials {
                username = providers.gradleProperty("gpr.user").getOrNull()
                password = providers.gradleProperty("gpr.key").getOrNull()
            }
        }
    }
}
```

**app/build.gradle**

```groovy
implementation 'io.surepass.sdk:esign-android-sdk-v2:1.0.9'
```

**AndroidManifest.xml**: the Protean AAR declares its own theme, so replace it.

```xml
<application
    android:theme="@style/Theme.YourApp"
    tools:replace="android:theme">
```

eMudhra sets `android:networkSecurityConfig="@xml/network_security_config"`. If your config has
another name, add `android:networkSecurityConfig` to `tools:replace`; with none, eMudhra's applies
to your whole app.

## Usage

```kotlin
private val eSign = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
    val json = result.data?.getStringExtra(InitSDK.EXTRA_SIGNED_RESPONSE)
}

eSign.launch(InitSDK.newIntent(this, token, InitSDK.ENV_PREPROD)) // or InitSDK.ENV_PROD
```

The SDK always finishes with `RESULT_OK` and a JSON envelope: `status_code`, `data`, `error`,
`message`.

| `status_code` | Meaning |
|---|---|
| `200` | Signed. `data` is the signed document URL, or `""` when downloads are disabled. |
| `433` | The user exited before signing. |
| `401` | Missing or invalid token/env, an expired session, or no connection at start-up. |
| `400`, `422` | The backend rejected the session's prefilled mobile number. |
| `403` | Too many attempts. |
| `404`, other `4xx` | The backend rejected the session. |
| `450` | No usable signing backend for the session. |
| `5xx` | The backend failed at start-up. |
| `501` | Signing failed in a way a retry won't fix, or Protean rejected the environment (NSDL signs in `PROD` only). |

## Notes

- The OTP is read from its SMS with the user's consent. This needs Google Play services; without
  them the user types the code.
- eMudhra signing runs only on ARM devices.
- R8/ProGuard needs no extra rules.

## Running the sample

Set `gpr.user` / `gpr.key`, put your token in `MainActivity` (`"YOUR TOKEN"`), and run `app`.
