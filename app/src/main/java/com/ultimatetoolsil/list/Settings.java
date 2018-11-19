package com.ultimatetoolsil.list;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.ultimatetoolsil.list.MainActivity.auth;
import static com.ultimatetoolsil.list.MainActivity.myRef;
import static com.ultimatetoolsil.list.MainActivity.userID;
import static com.ultimatetoolsil.list.createprofile.isvalidusername;

/**
 * Created by mike on 22 Apr 2018.
 */

public class Settings extends PreferenceActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        Preference username=(Preference)getPreferenceManager().findPreference("usnn");
        username.setSummary(getIntent().getStringExtra("username"));
        username.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final EditText taskEditText = new EditText(Settings.this);
                AlertDialog dialog = new AlertDialog.Builder(Settings.this)
                        .setTitle(getResources().getString(R.string.change_user))
                        //.setMessage("What do you want to do next?")
                        .setView(taskEditText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newe= String.valueOf(taskEditText.getText());
                                if(isvalidusername(newe,Settings.this)) {
                                    myRef.child("users").child(userID).child("username").setValue(newe);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                return false;
            }
        });
       Preference logout=(Preference)getPreferenceManager().findPreference("log");
       logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
           @Override
           public boolean onPreferenceClick(Preference preference) {

               auth.signOut();
               Intent i=new Intent(Settings.this,login.class);
               startActivity(i);
               finish();
               Toast.makeText(Settings.this,getResources().getString(R.string.log_out),Toast.LENGTH_SHORT).show();
               return false;
           }
       });
        Preference policy=(Preference)getPreferenceManager().findPreference("police");
        policy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Uri webpage = Uri.parse("https://docs.google.com/document/d/1gSDBHuhHfezKYusC4FkvItSKuISUllvaLXISC8ejb-c/edit?usp=sharing" +
                        "");
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);

                }
                return false;
            }
        });
    }


}
