package com.vishnu.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Vishnu on 8/1/2015.
 */
public class PreferenceManager {

    private static Context sAppContext = EhsApplication.getContext();

    private static Gson gson = new Gson();

    private static SharedPreferences sPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(
            sAppContext);


    public static void setObject(String key, Object customObj) {
        String jsonStr = gson.toJson(customObj);
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(key, jsonStr);
        editor.apply();
    }

    public static <T> T getObject(String key, Class<T> customClass) {
        return gson.fromJson(sPreferences.getString(key, ""), customClass);

    }

    public static void setString(String key, String value) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(String key) {
        return sPreferences.getString(key, null);
    }

    public static void setInt(String key, int value) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(String key) {
        return sPreferences.getInt(key, 0);
    }

    public static void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(String key) {
        return sPreferences.getBoolean(key, false);
    }

    public static void clearPreference() {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Converts the ArrayList of values into JsonString and saves the string in system preference
     *
     * @param key  The key to save the string
     * @param list The list to convert
     * @param <T>  The type of the list
     */
    public static <T> void setArrayList(String key, ArrayList<T> list) {
        String jsonStr = gson.toJson(list);
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(key, jsonStr);
        editor.apply();
    }

    /**
     * Returns the ArrayList of values that was already saved in preference.
     *
     * @param key       The key to retrieve the list
     * @param arrayList The arrayList of the array list. The saved string from preference will be converted to the
     *                  arraylist using this arraylist
     * @param <T>       The arrayList of each value in the arrayList
     * @return The ArrayList <T> values.
     */
    public static <T> ArrayList<T> getArrayList(String key, Type arrayList) {
        ArrayList<T> retList = gson.fromJson(sPreferences.getString(key, ""), arrayList);

        // return an empty list if the stored respone is null;
        if (retList == null) {
            retList = new ArrayList<>();
        }
        return retList;
    }
}
