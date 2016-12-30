package com.novext.taxerapp.models;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.novext.taxerapp.App;

/**
 * Created by angel on 10/3/16.
 */

public class States {
    static String LOGIN = "login";
    static String USERID = "userid";
    static String START = "start";
    static String ACCESSTOKEN = "accesstoken";

    private static SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
    private static SharedPreferences.Editor edit = prefs.edit();

    public static void login(boolean state){
        edit.putBoolean(LOGIN,state);
        edit.commit();
    }

    public static void setUserId(String userId){
        edit.putString(USERID,userId);
        edit.commit();
    }

    public static String getUserId(){
        return prefs.getString(USERID,"");
    }

    public static boolean isLogged(){
        return prefs.getBoolean(LOGIN,false);
    }

    public static void started(boolean state){
        edit.putBoolean(START,state);
        edit.commit();
    }

    public static void setAccessToken(String accessToken){
        edit.putString(ACCESSTOKEN,accessToken);
        edit.commit();

    }

    public static String getAccessToken(){
        return prefs.getString(ACCESSTOKEN,"");
    }

    public static boolean alreadyStart(){
        return prefs.getBoolean(START,false);
    }
}
