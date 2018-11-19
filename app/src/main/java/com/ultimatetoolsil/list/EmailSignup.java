package com.ultimatetoolsil.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by mike on 28 Feb 2018.
 */

public class EmailSignup extends AppCompatActivity {
    private EditText inputEmail, inputPassword,inputUsername;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseDatabase mFirebaseDatabase;
    private String userID;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailsignup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();


        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputUsername=(EditText)findViewById(R.id.nicknme);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmailSignup.this, forgotpassword.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EmailSignup.this, login.class);
                startActivity(i);
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();

                String password = inputPassword.getText().toString().trim();

                String nickname = inputUsername.getText().toString().trim();

                if (TextUtils.isEmpty(email) || !isValidEmail(inputEmail.getText().toString())) {
                    Toast.makeText(getApplicationContext(), R.string.enter_email_address, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), R.string.enter_password, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), R.string.pass_short, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(nickname) ) {
                    Toast.makeText(getApplicationContext(), R.string.empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                //create User
                try {


                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(EmailSignup.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    FirebaseUser user = auth.getCurrentUser();
                                    userID = user.getUid();
                                    myRef.child("users").child(userID).setValue(userID);
                                    myRef.child("users").child(userID).child("email").setValue(inputEmail.getText().toString().trim());
                                    myRef.child("users").child(userID).child("username").setValue(inputUsername.getText().toString().trim());
                                    Toast.makeText(EmailSignup.this, "account created succsessfully" , Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    // If sign in fails, display a message to the User. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in User can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(EmailSignup.this, "Authentication failed." + task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                    } else {


                                        startActivity(new Intent(EmailSignup.this, MainActivity.class));
                                        finish();
                                    }
                                }
                            });

                } catch (Exception e) {

                    Toast.makeText(EmailSignup.this, R.string.user_exist, Toast.LENGTH_SHORT).show();
                }
            }


        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }


    public final boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
