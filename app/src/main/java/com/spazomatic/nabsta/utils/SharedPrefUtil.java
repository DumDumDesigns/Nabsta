package com.spazomatic.nabsta.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.spazomatic.nabsta.NabstaApplication;

/**
 * Created by samuelsegal on 6/11/15.
 */
public class SharedPrefUtil {

    private static String sharedPrefName = NabstaApplication.NABSTA_SHARED_PREFERENCES;

    public static boolean containsKey(Context context, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                sharedPrefName,Context.MODE_PRIVATE);
        return sharedPreferences.contains(key);
    }
    public static Long getLongValue(Context context, String key) throws NullPointerException{
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                sharedPrefName,Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, 0);
    }
    public static boolean setLongValue(Context context, String key, Long value) throws NullPointerException{
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                sharedPrefName,Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        return editor.commit();
    }
    public static Boolean getBooleanValue(Context context, String key, boolean defaultValue) throws NullPointerException{
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                sharedPrefName,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }
    public static boolean setBooleanValue(Context context, String key, Boolean value) throws NullPointerException{
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                sharedPrefName,Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

}
