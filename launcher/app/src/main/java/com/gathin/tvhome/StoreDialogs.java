package com.gathin.tvhome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.provider.Settings;

/**
 * The two store questions and the privacy policy. Separate dialogs: the accessibility
 * question is not mixed into the Wi-Fi one. Back and Not now record a decline.
 */
final class StoreDialogs {
    interface Answered {
        void onAnswered();
    }

    private StoreDialogs() {}

    static void show(Activity activity, String kind, Answered answered) {
        if (StoreConsent.KIND_PRIVACY.equals(kind)) {
            showPrivacy(activity);
            return;
        }
        boolean data = StoreConsent.KIND_DATA.equals(kind);
        boolean[] handled = {false};
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity)
                .setTitle(data ? R.string.store_data_title : R.string.store_a11y_title)
                .setMessage(data ? R.string.store_data_body : R.string.store_a11y_body)
                .setPositiveButton(R.string.store_agree, (d, w) -> {
                    handled[0] = true;
                    StoreConsent.choose(activity, data ? StoreConsent.DATA : StoreConsent.A11Y,
                            StoreConsent.YES);
                    if (!data) openAccessibilitySettingsIfHelpersAreOff(activity);
                    answered.onAnswered();
                })
                .setNegativeButton(R.string.store_not_now, (d, w) -> {
                    handled[0] = true;
                    StoreConsent.choose(activity, data ? StoreConsent.DATA : StoreConsent.A11Y,
                            StoreConsent.NO);
                    answered.onAnswered();
                })
                .setOnCancelListener(d -> {
                    if (handled[0]) return;
                    StoreConsent.choose(activity, data ? StoreConsent.DATA : StoreConsent.A11Y,
                            StoreConsent.NO);
                    answered.onAnswered();
                });
        if (data) {
            dialog.setNeutralButton(R.string.store_privacy, (d, w) -> {
                handled[0] = true;
                showPrivacy(activity, () -> show(activity, StoreConsent.KIND_DATA, answered));
            });
        }
        dialog.show();
    }

    static void showPrivacy(Activity activity) {
        showPrivacy(activity, null);
    }

    private static void showPrivacy(Activity activity, Runnable after) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.store_privacy)
                .setMessage(R.string.store_privacy_body)
                .setPositiveButton(R.string.store_close, null);
        if (after != null) dialog.setOnDismissListener(d -> after.run());
        dialog.show();
    }

    /** Agree does not enable a helper. Offer the system screen only when neither helper is on. */
    private static void openAccessibilitySettingsIfHelpersAreOff(Activity activity) {
        String enabled = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabled != null && (enabled.contains("InputKeyService") || enabled.contains("HomeKeyService"))) {
            return;
        }
        try {
            activity.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        } catch (ActivityNotFoundException | SecurityException ignored) {
        }
    }
}
