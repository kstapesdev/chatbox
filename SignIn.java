package com.scatterform.chatabox;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.BoringLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class SignIn extends AppCompatActivity {
    final String TAG = "ChattyTest-SignIn";

    EditText mUserEmail;
    EditText mUserPassword;
    EditText mUserConfirmPassword;
    EditText mUserDisplayName;

    Button mUserLogin;
    TextView mUserRegister;

    Boolean mLoginInProgress = false;
    Boolean mRegisterInProgress = false;

    String mDisplayName;

    FirebaseApp mApp;
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Log.e(TAG, "Sign In Activity");

        initDisplayControls();
        initListeners();
        initFirebase();
    }

    private void initDisplayControls() {

        //Set view variables
        mUserEmail = findViewById(R.id.userEmail);
        mUserPassword = findViewById(R.id.userPassword);
        mUserConfirmPassword = findViewById(R.id.userPasswordConfirm);
        mUserDisplayName = findViewById(R.id.userDisplayName);

        mUserLogin = findViewById(R.id.userLogin);
        mUserRegister = findViewById(R.id.userRegister);

        //Start with text views invisible
        mUserEmail.setVisibility(View.GONE);
        mUserPassword.setVisibility(View.GONE);
        mUserConfirmPassword.setVisibility(View.GONE);
        mUserDisplayName.setVisibility(View.GONE);

    }

    private void initListeners(){

        mUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRegisterInProgress = false;

                if(!mLoginInProgress) {
                    //first time logon hit

                mUserEmail.setVisibility(View.VISIBLE);
                mUserPassword.setVisibility(View.VISIBLE);

                mLoginInProgress = true;

                } else {
                    //Logon button hit again
                    String email = mUserEmail.getText().toString();
                    String password = mUserPassword.getText().toString();

                    loginUser(email,password);
                }
            }
        });

        mUserRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginInProgress = false;

                if(!mRegisterInProgress){
                    //first time register hit

                    mUserEmail.setVisibility(View.VISIBLE);
                    mUserPassword.setVisibility(View.VISIBLE);
                    mUserConfirmPassword.setVisibility(View.VISIBLE);
                    mUserDisplayName.setVisibility(View.VISIBLE);

                    mRegisterInProgress = true;

                } else {
                    //register hit again
                    String email = mUserEmail.getText().toString();
                    String password = mUserPassword.getText().toString();
                    String confirmPassword = mUserConfirmPassword.getText().toString();

                    mDisplayName = mUserDisplayName.getText().toString();

                    registerNewUser(email,password,mDisplayName);
                }
            }
        });
    }

    private void registerNewUser(String email, String password, final String displayName){

        OnCompleteListener<AuthResult> complete = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful())
                    Log.e(TAG, "SignIn : User registered ");
                else
                    Log.e(TAG, "SignIn : User registration response, but failed ");
            }
        };

        OnFailureListener failure = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"SignIn : Register user failure");
            }
        };

        Log.e(TAG, "SignIn : Registering : eMail [" + email + "] password [" + password + "] Display Name [" + displayName + "]");
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(complete).addOnFailureListener(failure);

    }

    private void loginUser(String email, String password){
        OnCompleteListener<AuthResult> login = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.e(TAG, "Sign in successful");
                } else {
                    Log.e(TAG, "Sign in failed");
                }
            }
        };

        OnFailureListener fail = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Login User call failed");
            }
        };

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(login).addOnFailureListener(fail);

    }

    private void initFirebase(){
        mApp = FirebaseApp.getInstance();
        mDatabase = FirebaseDatabase.getInstance(mApp);
        mAuth = FirebaseAuth.getInstance(mApp);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();

                if(user != null) {
                    Log.e(TAG, "User is logged In : Email [" + user.getEmail() + "] Display Name [" + user.getDisplayName() + "]");
                    mDisplayName = user.getDisplayName();

                    if(mRegisterInProgress){
                        setDisplayName(user);
                    } else {
                        mDisplayName = user.getDisplayName();
                    }

                    finishActivity();
                } else {
                    Log.e(TAG, "No User Is Logged In");
                    mDisplayName = "Anonymous";
                }

            }
        };

        mAuth.addAuthStateListener(mAuthListener);
    }

    private void setDisplayName(FirebaseUser user) {
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder().setDisplayName(mDisplayName).build();
        user.updateProfile(changeRequest);
    }

    private void finishActivity(){

        Log.e(TAG, "Finish Sign In Activity");

        Intent returningIntent = new Intent();
        returningIntent.putExtra("display name", mDisplayName);
        setResult(RESULT_OK,returningIntent);

        finish();

    }
}
