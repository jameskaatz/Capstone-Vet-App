package com.example.vetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null) {
            Log.d("AUTH", "User already logged in.");
            //change activity to correct view
            //to test just go straight to the farmer view
            startActivity(new Intent(this, FarmView.class));
        } else {
            Log.d("AUTH", "User not logged in.");
            //do the login stuffs
            createSignInIntent();
        }
    }

    public void createSignInIntent() {
        //Authentication Providers
        // When adding providers make sure you enable them in the firebase console
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(), //sign in by email
                new AuthUI.IdpConfig.PhoneBuilder().build(), //sign in by phone number
                new AuthUI.IdpConfig.GoogleBuilder().build() //sign in by google account
        );

        //create and launch the sign in intent
        startActivityForResult(
                AuthUI.getInstance()
                      .createSignInIntentBuilder()
                      .setAvailableProviders(providers)
                      .build(),
                RC_SIGN_IN
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if(resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseUserMetadata md = user.getMetadata();
                if(md.getCreationTimestamp() == md.getLastSignInTimestamp()){
                    //new user go to main activity view
                    Log.d("AUTH", "New User!!!");
                    //go to main activity
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    //existing user go to the respective farmer or vet view
                    Log.d("AUTH", "Existing User!!!");
                    //just for testing go to the farm view
                    //TODO: query database for the user to determine which view
                    startActivity(new Intent(this, FarmView.class));
                    finish();
                }
            } else {
                //sign in failed
                if(response == null) {
                    Log.d("AUTH", "Sign-In Cancelled.");
                    return;
                }

                if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Log.d("AUTH", "No Internet Connection.");
                    return;
                }

                //we can catch more errors if we want
                //unknown error
                Log.d("AUTH", "Unknown Sign-In Error", response.getError());
            }
        }
    }
}
