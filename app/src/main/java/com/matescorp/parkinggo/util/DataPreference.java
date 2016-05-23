package com.matescorp.parkinggo.util;

import android.content.SharedPreferences;

public class DataPreference {

    public static SharedPreferences PREF = null;
    public final static String TAG = "DataPreference";

    public static void setLoginId(String value) {
        if (PREF != null) {
            SharedPreferences.Editor editor = PREF.edit();
            editor.putString(Config.PREF_LOGIN_KEY, value);
            editor.commit();
        } else {
            Logger.d(TAG, "PREF == " + PREF);
        }
    }

    public static void setPassWord(String value) {
        if (PREF != null) {
            SharedPreferences.Editor editor = PREF.edit();
            editor.putString(Config.PREF_PASS_WORDK_KEY, value);
            editor.commit();
        } else {
            Logger.d(TAG, "PREF == " + PREF);
        }
    }

    public static void setGwidx(String value){
        if (PREF != null) {
            SharedPreferences.Editor editor = PREF.edit();
            editor.putString(Config.PREF_GWIDX_KEY, value);
            editor.commit();
        } else {
            Logger.d(TAG, "PREF == " + PREF);
        }
    }

    public static void setPush(boolean check){
        if (PREF != null) {
            SharedPreferences.Editor editor = PREF.edit();
            editor.putBoolean(Config.PREF_PUSH_NOTIFICATION_KEY, check);
            editor.commit();
        } else {
            Logger.d(TAG, "PREF == " + PREF);
        }
    }

    public static String getLoginId() {
        return PREF != null ? PREF.getString(Config.PREF_LOGIN_KEY, null) : null;
    }
    public static String getPassword() {
        return PREF != null ? PREF.getString(Config.PREF_PASS_WORDK_KEY, null) : null;
    }

    public static String getGwdix() {
        return PREF != null ? PREF.getString(Config.PREF_GWIDX_KEY, null) : null;
    }

    public static boolean getPush() {
        return PREF != null ? PREF.getBoolean(Config.PREF_PUSH_NOTIFICATION_KEY, true) : true;
    }

}
