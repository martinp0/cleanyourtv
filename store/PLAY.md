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
| Permissions | `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE`, `ACCESS_FINE_LOCATION` (max SDK 32), `NEARBY_WIFI_DEVICES` (`neverForLocation`), `WRITE_SECURE_SETTINGS` (not grantable from Play), `yandex.tv.permission.ACCESS_TV_SOURCE` |
| Package visibility | `<queries>` for launchable apps, settings, and a few named packages. Not `QUERY_ALL_PACKAGES`. |
| Accessibility | `HomeKeyService` and `InputKeyService`. Off until enabled. Not `isAccessibilityTool`. |
| Release signing | The release build type still uses the debug key so a sideload can install. Play will reject that signature. Martin’s upload key stays off this repo. |

## Policy findings

### Target API level

[Target API level requirements](https://support.google.com/googleplay/android-developer/answer/11926878) and [Meet Google Play's target API level requirement](https://developer.android.com/google/play/requirements/target-sdk).

As of 31 August 2026, new apps and updates must target **API 36**, except Android TV apps, which must target **API 34** or higher. The extension window closed on 1 November 2026. Today is 13 November 2026.

This build targets 34. That meets the **Android TV** rule and does **not** meet the **phone** rule. Do not submit a phone listing until the target is 36. This PR does not bump it: CI installs platform 34 only, and targeting 36 opts every sideloaded TV into newer platform behavior without a run on a real set.

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

Launchers are named as apps that are **not** accessibility tools. Do not set `isAccessibilityTool=true`. The helpers are deterministic (one remote key, one action), which the automation section allows, but they still need:

- a separate in-app disclosure in normal use, with Agree and Not now (this PR)
- the Play Console accessibility declaration
- a review video of the disclosure, the decline path, the accept path, and a helper actually handling a remote key

The services receive key codes only. They do not read the screen. Answer the declaration: purpose **App functionality**; collect or share personal data with the accessibility API **No**.

Risk that remains: Play has rejected launchers that use this API to take the Home key. The disclosure makes the use honest. It does not guarantee approval. If you can ship the TV listing without the two services, the review is simpler. Removing them would break remotes that need them, so this PR leaves them in and gates them on Agree.

### QUERY_ALL_PACKAGES

[Package visibility permission](https://support.google.com/googleplay/android-developer/answer/10158779).

Not declared. The manifest uses `<queries>`, which is the narrower method the policy points people to. No declaration form for this permission. A launcher that can see launchable apps is the core of the home screen. The list is not sent off the TV.

### User data, location, and the privacy policy

[User Data](https://support.google.com/googleplay/android-developer/answer/10144311), [prominent disclosure](https://support.google.com/googleplay/android-developer/answer/11150561), [prepare your app for review](https://support.google.com/googleplay/android-developer/answer/9859455).

Every app needs a privacy policy URL in Play Console and the policy text in the app, even when it collects nothing. The in-app text is Launcher settings → Privacy policy, and the Wi-Fi question links to it. You still have to **host** [privacy-policy-en.md](privacy-policy-en.md) and paste that URL. Do not reuse https://cleanyour.tv/app/android/privacy — that page describes the other phone app.

The Wi-Fi name is not something a person necessarily expects a home screen to read, so the app asks before reading it. Not now leaves the button as “Wi-Fi”. On Android 13 and newer the location permission is no longer in the installed manifest (`maxSdkVersion="32"`). `NEARBY_WIFI_DEVICES` is declared `neverForLocation`.

`WRITE_SECURE_SETTINGS` is a signature permission. Play will not grant it. Theme push and putting the helpers back after a force-stop keep working for sideload, where the install script can grant it. They will not work for a Play install. Leaving the permission declared is for the sideload build. If the console warns, remove it only from the artifact you upload, not from the sideload manifest, or sideload repair breaks.

### Permissions policy

[Permissions and APIs that access sensitive information](https://support.google.com/googleplay/android-developer/answer/9888170) and [restricted permissions](https://support.google.com/googleplay/android-developer/answer/9888170).

No SMS, call log, or install-packages permission. Location is foreground-only, capped at API 32, and explained in the disclosure. No background location.

## Samsung Galaxy Store

[Galaxy Store app distribution guide](https://developer.samsung.com/galaxy-store/distribution-guide.html).

Galaxy Store sells Android apps for Galaxy phones, tablets, and watches. Samsung TVs run Tizen and are a different store. This launcher does not run on Tizen, and it is not a phone remote.

Relevant review rules from that guide: the binary must not hide features (1.1.3), and it must not break device features such as Wi-Fi or the volume keys (1.4.4). A landscape TV home screen on a Galaxy phone fails the “does what the listing says, on this device” test. The accessibility helpers can swallow remote keys; on a phone those are not TV remotes, and a reviewer can treat that as interference.

Recommendation: **do not submit this APK to Galaxy Store.** A phone companion that drives a TV would be a different app, a different review, and a higher policy risk because it would be modifying another device.

## What this PR changes

- First launch asks about the Wi-Fi name, then, on a separate screen, about the remote-button helpers. Agree and Not now are both real answers. Back is Not now.
- The Wi-Fi name is read only after Agree. The helpers ignore keys until Agree.
- Location permission stops at Android 12. Android 13 and newer use nearby Wi-Fi devices, still only after Agree.
- Privacy policy text is in the app. Czech titles and buttons are translated. The paragraphs stay in English: the 480 KB size gate cannot hold a second full copy. The Czech policy to host is [privacy-policy-cs.md](privacy-policy-cs.md).
- Version 2.8 (9).

Sideload behavior change: the next time Home opens after this update, those two questions appear once. Helpers that were already enabled do nothing until Agree. The install script still grants the Wi-Fi permission so the system dialog does not have to appear when the grant is already there.

## What Martin still decides

1. **Android TV only, or also phones.** Phones need targetSdk 36, a phone layout, and a CI image that has platform 36. This PR does not do that.
2. **Host the privacy policy** at a stable HTTPS URL on a site he controls. Paste it into Play Console. Match the developer name on the account with “Martin Pohl” or edit the policy.
3. **Sign outside git.** Build an Android App Bundle, sign it with the upload key, enroll Play App Signing. Do not commit the key. The debug-signed release APK in this repo is for sideload only.
4. **Record the accessibility review video** on a real TV, or remove the two services from the uploaded artifact if he would rather not defend them.
5. **Complete the accessibility declaration** in Play Console (not an accessibility tool; app functionality; no data collected or shared).
6. **Store listing** from [listing-en.md](listing-en.md) and [listing-cs.md](listing-cs.md). Category Personalization. No debloat claims.
7. **Data safety and content rating** from the two drafts. Ads: no. Designed for Families: no.
8. **Real TV pass** of the two new screens with the remote, from every edge, before relying on this as Home. This change was checked with unit tests and an emulator, not a television.
9. **Galaxy Store:** skip, unless a different phone app is in scope.
10. **On-device name** stays “Home” / “Domů”. Say if the store title should match it instead of CleanYourTV.
