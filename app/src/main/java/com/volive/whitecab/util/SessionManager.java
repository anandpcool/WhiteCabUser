package com.volive.whitecab.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import com.volive.whitecab.Activities.LoginActivity;

import java.util.HashMap;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    Context context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "ontime";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "firstname";
    public static final String KEY_LASTNAME = "lastname";
    public static final String KEY_ID = "id";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_IMAGEURI = "noImage";
    public static final String KEY_ROLE = "role";
    public static final String KEY_PASSWORD = "pwd";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_Location = "location";
    public static final String KEY_LANGUAGE = "language";
    public static final String KEY_NUMBER = "Number";
    public static final String KEY_PACK_DETAIL_STATUS = "pack_detaill_status";


    public static final String TAG = "tag";

    public static final String PROFILE_IMG_URL = "profile_img_url";
    public static final String PROFILE_IMG_PATH = "profile_img_url_path";

    // Constructor
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     *  *
     */

    public void LoginSuccess() {

        editor.putBoolean(IS_LOGIN, true);
        editor.commit();

    }

    // TODO  pack_detaill_status =1 => upgrade package,pack_detaill_status = 2 => package details


    public void createLoginSession(String id, String email, String name, String pwd, String number, boolean login) {
        // Storing login value as TRUE

        // Storing name in pref
        editor.putString(KEY_ID, id);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_PASSWORD, pwd);
        editor.putString(KEY_NUMBER, number);
        editor.putBoolean(IS_LOGIN, login);
        editor.commit();
    }

    public String getSingleField(String key) {
        return pref.getString(key, null);
    }

    public void createEditProfileSession(String email, String name, String pwd, String gender, String location) {
        // Storing login value as TRUE

        // Storing name in pref
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_GENDER, gender);
        editor.putString(KEY_Location, location);
        editor.putString(KEY_PASSWORD, pwd);

        editor.commit();
    }

    // TODO 1 = english 2 = arbic

    public void setLanguage(String language) {
        // Storing login value as TRUE

        // Storing name in pref
        editor.putString(KEY_LANGUAGE, language);

        editor.commit();
    }

    /**
     *  * Check login method wil check user login status
     *  * If false it will redirect user to login page
     *  * Else won't do anything
     *  *
     */
    public void checkLogin() {
        if (!this.isLoggedIn()) {
            Intent i = new Intent(context, LoginActivity.class);
            i.putExtra("isComeFromForgot", false);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    /**
     *  * Get stored session data
     *  *
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name

        user.put(KEY_ID, pref.getString(KEY_ID, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));
        user.put(KEY_LANGUAGE, pref.getString(KEY_LANGUAGE, ""));
        user.put(KEY_NUMBER, pref.getString(KEY_NUMBER, null));

        // return user
        return user;
    }

    /**
     *  * Clear session details
     *  *
     */
    public void logoutUser() {
        //Clearing all data from Shared Preferences

        editor.remove(IS_LOGIN);
        editor.remove(KEY_NAME);
        editor.remove(KEY_PASSWORD);
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_ID);
        editor.remove(KEY_NUMBER);
        editor.remove(PROFILE_IMG_URL);
        editor.remove(PROFILE_IMG_PATH);
//        editor.remove(KEY_LANGUAGE);
        editor.commit();

        Intent i = new Intent(context, LoginActivity.class);
//        i.putExtra("isComeFromForgot", false);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
        ((Activity) context).finish();

    }

    public void logoutpackged() {
        //Clearing all data from Shared Preferences

        editor.remove(IS_LOGIN);
        editor.remove(KEY_NAME);
        editor.remove(KEY_PASSWORD);
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_ID);
//        editor.remove(KEY_IMAGEURI);
        editor.remove(KEY_ROLE);
        editor.remove(KEY_GENDER);
        editor.remove(KEY_Location);
        editor.remove(PROFILE_IMG_URL);
        editor.remove(PROFILE_IMG_PATH);
//        editor.remove(KEY_LANGUAGE);
        editor.remove(KEY_PACK_DETAIL_STATUS);
        editor.commit();

       /* Intent i = new Intent(context, SelectLanguage.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
        ((Activity) context).finish();*/

    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void profileImageUrl(String profile_url, String path) {
        Log.e(" profile_url : ", path + profile_url);
        editor.putString(PROFILE_IMG_URL, profile_url);
        editor.putString(PROFILE_IMG_PATH, path);
        editor.commit();
    }

    public HashMap<String, String> returnProfile_url() {
        HashMap<String, String> profileurl = new HashMap<String, String>();

        profileurl.put(PROFILE_IMG_URL, pref.getString(PROFILE_IMG_URL, null));
        profileurl.put(PROFILE_IMG_PATH, pref.getString(PROFILE_IMG_PATH, null));

        return profileurl;
    }
}
