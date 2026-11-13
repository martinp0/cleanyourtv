package com.gathin.tvhome;

import android.content.Context;

/**
 * Choices for the two store disclosures ({@link DisclosureActivity}).
 * Unset means the question has not been asked yet. Not now is a real answer: Home keeps working,
 * and the matching read or remote-button helper stays off until the person agrees later.
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

    /** The next full-screen question, or null when both have been answered. Privacy is not in this chain. */
    static String nextKind(int data, int accessibility) {
        if (data == UNSET) return KIND_DATA;
        if (accessibility == UNSET) return KIND_ACCESSIBILITY;
        return null;
    }

    static boolean mayReadWifiName(int data) {
        return data == YES;
    }

    static boolean mayFilterKeys(int accessibility) {
        return accessibility == YES;
    }

    static String nextKind(Context context) {
        return nextKind(choice(context, DATA), choice(context, A11Y));
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
