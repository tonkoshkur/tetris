package ua.tonkoshkur.tetris.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {

    public static final String STORAGE_NAME = "shared_prefs";

    private static SharedPreferences settings = null;
    private static SharedPreferences.Editor editor = null;
    private static Context context = null;

    public static void init(Context cntxt) {
        context = cntxt;
    }

    private static void init() {
        settings = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
    }

    public static void addIntProperty(String name, int value) {
        if (settings == null) {
            init();
        }
        editor.putInt(name, value);
        editor.apply();
    }

    public static int getIntProperty(String name) {
        if (settings == null) {
            init();
        }
        return settings.getInt(name, 0);
    }

    public static void addBooleanProperty(String name, boolean value) {
        if (settings == null) {
            init();
        }
        editor.putBoolean(name, value);
        editor.apply();
    }

    public static boolean getBooleanProperty(String name) {
        if (settings == null) {
            init();
        }
        return settings.getBoolean(name, false);
    }
}