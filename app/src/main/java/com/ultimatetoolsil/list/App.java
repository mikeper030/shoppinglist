package com.ultimatetoolsil.list;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by mike on 1 Mar 2018.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
