package com.novext.taxerapp;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by angel on 9/30/16.
 */

public class App extends Application {

    private static OkHttpRequest okHttpRequest;
    private static App app;
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        okHttpRequest = new OkHttpRequest("https://taxerapi.herokuapp.com/");
        app = this;

        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().subscribeToTopic("stops");

    }

    public static App getInstance(){
        return app;
    }

    public static OkHttpRequest getInstanceOkHttpRequest(){
        return okHttpRequest;
    }
}
