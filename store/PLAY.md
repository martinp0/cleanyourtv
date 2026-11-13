# Play and Galaxy Store readiness

Verdict: **high risk of rejection** if this package is submitted as a phone app, or as a tool that cleans a TV over ADB. **OK with changes** only for an Android TV listing of the home screen, and only after the remaining items below. This PR does the in-app parts. It does not upload anything.

Assessed 13 November 2026 against the policy pages linked here. The app in this repo is the launcher `com.gathin.tvhome` (versionName 2.8, versionCode 9, minSdk 26, targetSdk 34, compileSdk 34). It is a home screen that runs on the TV. It does not open an ADB connection, does not ship `tools/tv.py`, and does not disable other packages.

The community phone app at cleanyour.tv/app (`tv.cleanyour.app`) is a different product. It does use ADB over Wi-Fi. It is not this repository and these drafts do not clear it for Play.

## What the launcher does

| | |
|---|---|
| Package | `com.gathin.tvhome` |
| Version | 2.8 (9) |
| SDK | min 26, target 34, compile 34 |
| Monetization | None. No ads, no billing, no account. A donation link exists only on the website, not in the app. |
| Network | No `INTERNET` permission. No analytics. |
| TV connection | None. A computer uses ADB separately. The launcher is already on the TV. |
| Play variant | Build type `play`. Android TV only. `./gradlew bundlePlay` writes the AAB. |
| Permissions in the Play manifest | `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE`, `ACCESS_FINE_LOCATION` (max SDK 32), `NEARBY_WIFI_DEVICES` (`neverForLocation`), `yandex.tv.permission.ACCESS_TV_SOURCE`. No `WRITE_SECURE_SETTINGS`. |
| Package visibility | `<queries>` for launchable apps, settings, and a few named packages. Not `QUERY_ALL_PACKAGES`. |
| Accessibility | Not in the Play variant. `HomeKeyService` and `InputKeyService` stay in the sideload `release` build only. |
| Signing | `play` uses the same debug signing config as `release`, so a local install works. The upload key stays off this repo. Sign the AAB locally before you upload. |

## Policy findings

### Target API level

[Target API level requirements](https://support.google.com/googleplay/android-developer/answer/11926878) and [Meet Google Play's target API level requirement](https://developer.android.com/google/play/requirements/target-sdk).

As of 31 August 2026, new apps and updates must target **API 36**, except Android TV apps, which must target **API 34** or higher. The extension window closed on 1 November 2026. Today is 13 November 2026.

This build targets 34. That meets the Android TV rule. The Play upload is Android TV only, so the phone rule (API 36) does not apply. Phones and tablets stay out of the listing.

### Device and network abuse

[Device and Network Abuse](https://support.google.com/googleplay/android-developer/answer/16559646).

The policy forbids interfering with the user’s device, other devices, or other apps, including circumventing security protections and installing or modifying other apps without consent.

The **launcher APK** does not talk to another device, does not run ADB, and does not disable packages. A store listing that says the download debloats a TV, turns on wireless debugging, or disables system apps would describe a different program and would conflict with this policy. The computer tools stay out of the listing. See [listing-en.md](listing-en.md).

### Deceptive behavior

[Deceptive behaviour](https://support.google.com/googleplay/android-developer/answer/17006354).

Section 2.1: changes to device settings, including how apps are presented on the home screen, need the user’s knowledge and consent, and must be reversible. Section 2.2.4: do not encourage people to disable other apps unless you are a verifiable security service. This launcher is not one.

The Play build does not set itself as the default home. Android’s home picker does, and Launcher settings opens that picker so the person can switch back. The listing must not tell people to disable the stock launcher or other apps. Metadata must match the app: a home screen, not a cleaner.

### Accessibility API

[Use of the AccessibilityService API](https://support.google.com/googleplay/android-developer/answer/10964491).

The Play variant does not declare `HomeKeyService` or `InputKeyService`. There is no accessibility declaration and no review video for this upload. The sideload `release` build still has both services, for remotes that need them, and still asks before they take a key. That APK is not the Play file.

### QUERY_ALL_PACKAGES

[Package visibility permission](https://support.google.com/googleplay/android-developer/answer/10158779).

Not declared. The manifest uses `<queries>`, which is the narrower method the policy points people to. No declaration form for this permission. A launcher that can see launchable apps is the core of the home screen. The list is not sent off the TV.

### User data, location, and the privacy policy

[User Data](https://support.google.com/googleplay/android-developer/answer/10144311), [prominent disclosure](https://support.google.com/googleplay/android-developer/answer/11150561), [prepare your app for review](https://support.google.com/googleplay/android-developer/answer/9859455).

Every app needs a privacy policy URL in Play Console and the policy text in the app, even when it collects nothing. The in-app text is Launcher settings → Privacy policy, and the Wi-Fi question links to it. You still have to **host** [privacy-policy-en.md](privacy-policy-en.md) and paste that URL. Do not reuse https://cleanyour.tv/app/android/privacy — that page describes the other phone app.

The Wi-Fi name is not something a person necessarily expects a home screen to read, so the app asks before reading it. Not now leaves the button as “Wi-Fi”. On Android 13 and newer the location permission is no longer in the installed manifest (`maxSdkVersion="32"`). `NEARBY_WIFI_DEVICES` is declared `neverForLocation`.

The Play manifest does not request `WRITE_SECURE_SETTINGS`. Theme push over adb and restoring the remote helpers after a force-stop stay on the sideload build, which still requests that permission and still declares `ThemeProvider`.

### Permissions policy

[Permissions and APIs that access sensitive information](https://support.google.com/googleplay/android-developer/answer/9888170) and [restricted permissions](https://support.google.com/googleplay/android-developer/answer/9888170).

No SMS, call log, or install-packages permission. Location is foreground-only, capped at API 32, and explained in the disclosure. No background location.

## Samsung Galaxy Store

[Galaxy Store app distribution guide](https://developer.samsung.com/galaxy-store/distribution-guide.html).

Galaxy Store sells Android apps for Galaxy phones, tablets, and watches. Samsung TVs run Tizen and are a different store. This launcher does not run on Tizen, and it is not a phone remote.

Relevant review rules from that guide: the binary must not hide features (1.1.3), and it must not break device features such as Wi-Fi or the volume keys (1.4.4). A landscape TV home screen on a Galaxy phone fails a phone review.

Recommendation: do not submit this APK to Galaxy Store. The Play upload is Android TV only.

## What this PR changes

- Build type `play` is the Android TV store artifact. `./gradlew bundlePlay` writes the AAB. It declares `android.software.leanback` required and `android.hardware.touchscreen` not required, and keeps the existing `LEANBACK_LAUNCHER` intent and the 320×180 banner.
- That manifest has no `InputKeyService`, no `HomeKeyService`, and no `WRITE_SECURE_SETTINGS`. The Play app does not ask about remote-button helpers. Launcher settings hides that row.
- The `release` build is still the sideload APK CI size-gates. It keeps the helpers and still asks before they take a key.
- First launch of either build asks about the Wi-Fi name. Agree and Not now are both real answers. Back is Not now. The name is read only after Agree.
- Version 2.8 (9).

## What is still left before upload

1. Host [privacy-policy-en.md](privacy-policy-en.md) at a stable HTTPS URL. Paste it into Play Console. Match the developer name on the account with “Martin Pohl”, or edit the policy.
2. Run `./gradlew bundlePlay`, then sign that AAB locally with the upload key. Do not commit the key. The debug signature in this repo is only so a local install works.
3. Create the Play app as Android TV only. Use [listing-en.md](listing-en.md) and [listing-cs.md](listing-cs.md). No phone listing, no debloat claims, no accessibility declaration.
4. Data safety and content rating from the two drafts. Ads: no. Designed for Families: no.
5. A pass on a real TV of the Wi-Fi question and Home, with the remote, before relying on the sideload build. The Play screens were checked on an emulator.
6. Galaxy Store: skip.
7. The on-device name stays “Home” / “Domů”. The store title in the drafts is CleanYourTV.
