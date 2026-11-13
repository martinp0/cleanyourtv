# Data safety answers

Play’s definition of **collected** is data transmitted off the device. **Shared** means transmitted to a third party. This launcher does neither. Source: [Understand app privacy & security practices with Google Play’s Data safety section](https://support.google.com/googleplay/android-developer/answer/10787469).

Answer the form as follows. If the console later flags a declared permission, use the note under that row. Do not switch the answers to “collected” unless you add a server.

| Question | Answer |
|---|---|
| Does your app collect or share any of the required user data types? | **No** |
| Is all of the user data collected by your app encrypted in transit? | Not shown when the answer above is No |
| Do you provide a way for users to request that their data be deleted? | Not shown when nothing is collected. There is no account. Uninstall or clear storage deletes the on-device choices. |
| Ads | No |
| In-app purchases | No |
| Privacy policy URL | The HTTPS URL where you host [privacy-policy-en.md](privacy-policy-en.md). Required even though nothing is collected. [User Data](https://support.google.com/googleplay/android-developer/answer/10144311) |

## If Play Console ties a permission to the form

The manifest still declares permissions. Collection stays No because the bytes do not leave the TV. If a reviewer asks for a declaration anyway, use this and do not mark any type as shared:

| Permission | Data type if forced to declare | Collected? | Shared? | Purpose | Notes |
|---|---|---|---|---|---|
| `ACCESS_FINE_LOCATION` (max SDK 32 only) | Location, approximate | No | No | App functionality | Used only to read the Wi-Fi name on Android 12 and older. Not GPS. Not background location. Ephemeral, on device. |
| `NEARBY_WIFI_DEVICES` with `neverForLocation` | Do not declare as location | No | No | App functionality | Android 13 and newer, Wi-Fi name only. |
| Package visibility (`<queries>`, not `QUERY_ALL_PACKAGES`) | App info, installed apps, if forced | No | No | App functionality | Launchable apps, to draw tiles. The broad permission is not requested. |

The Play artifact (`bundlePlay`) does not declare an accessibility service and does not request `WRITE_SECURE_SETTINGS`. Do not fill the accessibility declaration for that upload. The sideload release build still has both; it is not the file you upload.
| `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE` | None | No | No | App functionality | Online, Wi-Fi, or Ethernet, for the button label. Not transmitted. |

Account data, financial info, messages, photos, audio, files, calendar, contacts, web history, device IDs, and crash logs: not accessed.

Data the user can see in the app (the network name, the app tiles) is processed on the TV only.
