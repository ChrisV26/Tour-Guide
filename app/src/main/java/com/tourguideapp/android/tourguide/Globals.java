package com.tourguideapp.android.tourguide;

import android.content.Context;
import android.content.SharedPreferences;

/** Check if the user visits app for first time */

public class Globals
{
    private static SharedPreferences pref;

    // Shared preferences file name
    private static final String PREF_NAME = "TourGuide-Shared-Pref";

    private static final String IS_FIRST_TIME_LAUNCH = "FIRST_TIME_LAUNCH";

    public static void init(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static boolean shouldShowSlider() {
        return (pref == null) || pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public static void saveFirstTimeLaunch(boolean isFirstTime) {
        if (pref == null)
            return;

        SharedPreferences.Editor editor = pref.edit();
        if (editor != null) {
            editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
            editor.apply();
        }
    }
}
