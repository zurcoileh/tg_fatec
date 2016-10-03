package com.easy_ride.app.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.easy_ride.app.main.ERLoginActivity;

import java.util.HashMap;

/**
 * Created by Helio on 9/25/2016.
 */

public class UserSessionManager {

    private int PRIVATE_MODE = 0;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;

    private static final String PREFER_LOGIN = "LoginUser";
    private static final String IS_USER_LOGGED = "UserLogged";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_DIST_PREF = "distPreferences";
    public static final String KEY_DRIVER_MODE = "driver_mode";
    public static final String KEY_INVIS_MODE = "invis_mode";
    public static final String KEY_RA = "ra";
    public static final String KEY_LOCATIONS = "loc_keys";

    public UserSessionManager(Context context) {
        this.context = context;
        preferences = this.context.getSharedPreferences(PREFER_LOGIN, PRIVATE_MODE);
        this.editor = this.preferences.edit();
    }

    public void createUserSession(String email) {

        this.editor.putBoolean(IS_USER_LOGGED, true);
        this.editor.putString(KEY_EMAIL, email);
        this.editor.commit();

    }

    public boolean checkLogin() {
        if (!this.isUserLogged()) {
            Intent intent = new Intent(this.context, ERLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        return false;
    }

    public HashMap<String, String> getUser() {

        HashMap<String, String> usuario = new HashMap<String, String>();
        usuario.put(KEY_NAME, this.preferences.getString(KEY_NAME, null));
        usuario.put(KEY_EMAIL, this.preferences.getString(KEY_EMAIL, null));
        return usuario;

    }

    public void logoutUser() {

        this.editor.clear();
        this.editor.commit();

        Intent intent = new Intent(this.context, ERLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.context.startActivity(intent);

    }

    public boolean isUserLogged() {
        return this.preferences.getBoolean(IS_USER_LOGGED,false);
    }

    public void setSessionName(String key){
        this.editor.putString(KEY_NAME, key);
        this.editor.commit();
    }
    public void setSessionRa(String ra){
        this.editor.putString(KEY_RA, ra);
        this.editor.commit();
    }

    public void configDistPreferences(int distance) {
        this.editor.putInt(KEY_DIST_PREF, distance);
        this.editor.commit();
    }

    public void configDriverMode(int driver_mode) {
        this.editor.putInt(KEY_DRIVER_MODE, driver_mode);
        this.editor.commit();
    }

    public void configInvisMode(int invis_mode) {
        this.editor.putInt(KEY_INVIS_MODE, invis_mode);
        this.editor.commit();
    }

    public void setLocationKeys(String key){
        this.editor.putString(KEY_LOCATIONS, key);
        this.editor.commit();
    }

    public int getDistPreferences() {
        return this.preferences.getInt(KEY_DIST_PREF, 20);
    }

    public String getUserEmail()  {return this.preferences.getString(KEY_EMAIL, "NOT_FOUND");}
    public String getUserName()  {return this.preferences.getString(KEY_NAME, "NOT_FOUND");}
    public String getKeyLocations()  {return this.preferences.getString(KEY_LOCATIONS, "NOT_FOUND");}
    public String getUserRA()  {return this.preferences.getString(KEY_RA, "NOT_FOUND");}
    public int getDriverMode() {
        return this.preferences.getInt(KEY_DRIVER_MODE,0);
    }
    public int getInvisMode() {
        return this.preferences.getInt(KEY_INVIS_MODE,0);
    }



}

