package ua.tonkoshkur.tetris.utils;

import android.content.Context;
import android.content.SharedPreferences;

import ua.tonkoshkur.tetris.R;

public class SharedPrefs {

    private static SharedPreferences getSharedPrefs(Context context) {
        String sharedPrefsKey = context.getString(R.string.shared_prefs_key);
        return context.getSharedPreferences(sharedPrefsKey, Context.MODE_PRIVATE);
    }

    public static boolean getBoolean(Context context, int keyId) {
        return getBoolean(context, keyId, false);
    }

    public static boolean getBoolean(Context context, int keyId, boolean defaultValue) {
        String key = context.getString(keyId);
        return getSharedPrefs(context).getBoolean(key, defaultValue);
    }

    public static void putBoolean(Context context, int keyId, boolean value) {
        SharedPreferences.Editor editor = getSharedPrefs(context).edit();
        String key = context.getString(keyId);
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static int getInt(Context context, int keyId) {
        return getInt(context, keyId, 0);
    }

    public static int getInt(Context context, int keyId, int defaultValue) {
        String key = context.getString(keyId);
        return getSharedPrefs(context).getInt(key, defaultValue);
    }

    public static void putInt(Context context, int keyId, int value) {
        SharedPreferences.Editor editor = getSharedPrefs(context).edit();
        String key = context.getString(keyId);
        editor.putInt(key, value);
        editor.apply();
    }
}