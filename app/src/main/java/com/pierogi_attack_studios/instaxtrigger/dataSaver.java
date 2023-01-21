package com.pierogi_attack_studios.instaxtrigger;

import android.content.Context;
import android.content.SharedPreferences;

public class dataSaver {
    private static final String SHARED_PREFS = "sharedPrefs";
    public static final String MAC = "mac";

    static String loadStringData(MainActivity activity, String place) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(place, "");
    }

    static void saveData(MainActivity activity, String place, String data) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(place, data);
        editor.apply();
    }
}
