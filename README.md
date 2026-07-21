# Esign Android SDK - V2 Sample App

Sample application for eSign Android SDK - V2.

### Step to use the SDK below as well:

#### 1. settings.gradle :
```gradle
    pluginManagement {
        repositories {
            google()
            mavenCentral()
            gradlePluginPortal()
        }
    }
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
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
rootProject.name = "ESign Sample App"
include ':app'
```

#### 2. build.grade (app):
```groovy
   compileSdk 35
   minSdk 28 //min sdk should be 28

   compileOptions {
       sourceCompatibility JavaVersion.VERSION_17
       targetCompatibility JavaVersion.VERSION_17
   }
   kotlinOptions {
       jvmTarget = '17'
   }

   dependencies {
         implementation 'io.surepass.sdk:esign-android-sdk-v2:1.0.7'
   }
```
Make sure to sync your project after adding the dependency.

1.0.7 raises the toolchain, so upgrading from 1.0.6 is not just a version bump. The SDK is built
against AGP 8.6 / Kotlin 1.9.25 / Java 17 / compileSdk 35, and your project has to meet that:

| | 1.0.6 | 1.0.7 |
|---|---|---|
| Android Gradle Plugin | 8.0 | **8.6** |
| Gradle wrapper | 8.0 | **8.7** |
| Kotlin plugin | 1.8.0 | **1.9.25** |
| Java / `jvmTarget` | 8 | **17** |
| `compileSdk` | 34 | **35** |
| `minSdk` | 28 | 28 (unchanged) |

#### 3. AndroidManifest.xml (app):

Required. The bundled Protean AAR declares `android:theme` on its own `<application>` element, so
the manifest merger reports a conflict with yours and the build fails. Add `tools:replace` to
resolve it:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:theme="@style/Theme.YourApp"
        tools:replace="android:theme">
```

Overriding it is safe: the SDK pins each of its own and the vendors' screens to its internal theme
individually, so your theme applies to your UI only.

#### 4. Inside Application:
```kotlin
   binding.btnGetStarted.setOnClickListener {
        val token = "YOUR TOKEN"
        val env = InitSDK.ENV_PREPROD   // or InitSDK.ENV_PROD
        openActivity(env, token)
    }
```
SDK will be started from openActivity function
```kotlin
    import io.surepass.esign.ui.activity.InitSDK

    private fun openActivity(env: String, token: String) {
        eSignActivityResultLauncher.launch(InitSDK.newIntent(this, token, env))
    }
```
Response can be obtained in
```kotlin
   private fun registerActivityForResult() {
        eSignActivityResultLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
                ActivityResultCallback { result ->
                    val resultCode = result.resultCode
                    val data = result.data
                    if (resultCode == RESULT_OK && data != null) {
                        val eSignResponse = data.getStringExtra(InitSDK.EXTRA_SIGNED_RESPONSE)
                        Log.e("MainActivity", "eSign Response $eSignResponse")
                        showResponse(eSignResponse)
                    }
                })
    }
```
For better clarification you can check the code details inside the project
