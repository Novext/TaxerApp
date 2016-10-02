package com.novext.taxerapp;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

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
        okHttpRequest = new OkHttpRequest("hostname");
        app = this;
    }

    public static App getInstance(){
        return app;
    }

    public static OkHttpRequest getInstanceOkHttpRequest(){
        return okHttpRequest;
    }
}
