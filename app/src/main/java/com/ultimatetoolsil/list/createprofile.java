package com.ultimatetoolsil.list;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.ultimatetoolsil.list.signups.userID;

public class createprofile extends AppCompatActivity {
  Button cintinue;
  EditText nickname;
    private FirebaseAuth auth;
    private FirebaseDatabase mFirebaseDatabase;

    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createprofile);
        cintinue=(Button) findViewById(R.id.btn);
        nickname=(EditText) findViewById(R.id.name1);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        cintinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(nickname.toString())){
                    Toast.makeText(getBaseContext(),R.string.empty,Toast.LENGTH_SHORT).show();
                }else {
                     if(isvalidusername(nickname.getText().toString(),createprofile.this)) {
                         myRef.child("users").child(userID).child("username").setValue(nickname.getText().toString().trim());
                         myRef.child("users").child(userID).child("lists").child("shared").child("username").setValue(nickname.getText().toString().trim());
                         Intent main = new Intent(createprofile.this, MainActivity.class);
                         startActivity(main);
                         finish();
                     }
                }
                }
        });
    }
    public static boolean isvalidusername(String username, Context context) {
        if (username.toString().contains(" ")) {

            Toast.makeText(context, "No Spaces Allowed", Toast.LENGTH_SHORT).show();
            return false;

        } else {
            if (username.toString().indexOf('\n') > -1) {
                Toast.makeText(context, "No Spaces Allowed", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }
}
