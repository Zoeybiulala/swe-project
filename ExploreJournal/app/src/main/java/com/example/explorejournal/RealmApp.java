package com.example.explorejournal;

import android.app.Application;


import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;

public class RealmApp extends Application {

    public static App app = null;

    @Override
    public void onCreate() {
        super.onCreate();
        // user log in
        Realm.init(this);

        String appID = "explorejournal-jgwvj"; // replace this with your App ID
        app = new App(new AppConfiguration.Builder(appID)
                .build());
    }
}
