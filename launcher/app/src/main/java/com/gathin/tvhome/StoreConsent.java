package com.gathin.tvhome;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Choices for the store questions. Unset means the question has not been asked yet.
 * Not now is a real answer: Home keeps working, and the matching read stays off until Agree.
 * The remote-button question exists only when those services are in this build's manifest.
 */
final class StoreConsent {
    static final String DATA = "store_data_choice";
    static final String A11Y = "store_a11y_choice";
    static final int UNSET = 0;
    static final int YES = 1;
    static final int NO = 2;

    static final String KIND_DATA = "data";
    static final String KIND_ACCESSIBILITY = "accessibility";
    static final String KIND_PRIVACY = "privacy";

    private StoreConsent() {}

    /** Sideload: both questions. Play: Wi-Fi name only, because the helpers are not in that manifest. */
    static String nextKind(int data, int accessibility) {
        return nextKind(data, accessibility, true);
    }

    static String nextKind(int data, int accessibility, boolean remoteHelpers) {
        if (data == UNSET) return KIND_DATA;
        if (remoteHelpers && accessibility == UNSET) return KIND_ACCESSIBILITY;
        return null;
    }

    /** True when this install still declares InputKeyService. The Play manifest removes it. */
    static boolean hasRemoteHelpers(Context context) {
        try {
            context.getPackageManager().getServiceInfo(
                    new ComponentName(context.getPackageName(), "com.gathin.tvhome.InputKeyService"), 0);
            return true;
        } catch (PackageManager.NameNotFoundException missing) {
            return false;
        }
    }

    static boolean mayReadWifiName(int data) {
        return data == YES;
    }

    static boolean mayFilterKeys(int accessibility) {
        return accessibility == YES;
    }

    static String nextKind(Context context) {
        return nextKind(choice(context, DATA), choice(context, A11Y), hasRemoteHelpers(context));
    }

    static boolean mayReadWifiName(Context context) {
        return mayReadWifiName(choice(context, DATA));
    }

    static boolean mayFilterKeys(Context context) {
        return mayFilterKeys(choice(context, A11Y));
    }

    static int choice(Context context, String key) {
        return context.getSharedPreferences(Config.SETTINGS_NAME, Context.MODE_PRIVATE).getInt(key, UNSET);
    }

    /** Commit, not apply: the next screen reads the answer as soon as this one closes. */
    static void choose(Context context, String key, int value) {
        context.getSharedPreferences(Config.SETTINGS_NAME, Context.MODE_PRIVATE)
                .edit().putInt(key, value).commit();
    }
}
