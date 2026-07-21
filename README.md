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
            //enter github user name and github token
            maven {
                url = "https://maven.pkg.github.com/surepassio/esign-sample-app"
                credentials {
                    username = "USER_NAME"
                    password = "PAT_TOKEN"//https://docs.github.com/en/github/authenticating-to-github/keeping-your-account-and-data-secure/creating-a-personal-access-token
                    // (Allow Package Read Permission in token)
                }
            }
        }
    }
rootProject.name = "ESign Sample App"
include ':app'
```

Do not commit the token. Put it in `~/.gradle/gradle.properties` and read it here instead:

```gradle
    username = providers.gradleProperty("gpr.user").getOrNull()
    password = providers.gradleProperty("gpr.key").getOrNull()
```

From 1.0.7 the SDK pulls two additional packages from this same repository — the Protean/NSDL and
eMudhra signing libraries. They resolve automatically through the SDK's POM, so there is nothing
extra to declare, but the token above must be able to read packages or the build fails with a 401
on `protean-esign` / `emudhra-esign`.

#### 2. build.grade (app):
```groovy
   minSdk 28 //min sdk should be 28

   dependencies {
         implementation 'io.surepass.sdk:esign-android-sdk-v2:1.0.7'
   }
```
Make sure to sync your project after adding the dependency.

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
//            val token = binding.etApiToken.text.toString()
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

### Theme Of SDK

The SDK's screens are deliberately not themeable by the host app, and setting `colorPrimary` in your
`themes.xml` has no effect on them.

This changed in 1.0.7. The flow was rebuilt in Jetpack Compose against a fixed palette, and each of
the SDK's activities is pinned to its own theme, so nothing the host app declares reaches them. It is
intentional: the identity and eSign screens must look the same on every device, including in dark
mode, because they are what the signer is asked to trust.

Your own `themes.xml` still controls your own UI as normal — that is what step 3 above preserves.

Tenant branding, such as the logo shown during the flow, is served from your Surepass dashboard
rather than configured in the app.
