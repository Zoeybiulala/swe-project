package com.example.explorejournal;

import android.app.Application;


import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;

public class RealmApp extends Application {

    public static App app = null;
    public static final String appId = "explorejournal-ebrtn";

    @Override
    public void onCreate() {
        super.onCreate();
        // user log in
        Realm.init(this);

        app = new App(new AppConfiguration.Builder(appId).build());
    }
}
