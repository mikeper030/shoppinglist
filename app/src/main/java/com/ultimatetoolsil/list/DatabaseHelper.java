package com.ultimatetoolsil.list;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mike on 9 Mar 2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, "test_db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create tables
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop, alter tables
    }



}