package com.example.vetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button) findViewById(R.id.login_signup);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSignInIntent();
            }
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null) {
            Log.d("AUTH", "User already logged in.");
            //read from database and change view depending on the farmer boolean
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/users/" + auth.getCurrentUser().getUid());
            dbRef.addListenerForSingleValueEvent(new ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //change view based on received data
                    User user = dataSnapshot.getValue(User.class);
                    changeUserView(user);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { Log.d("AUTH", "User database read cancelled."); }
            });
        } else {
            Log.d("AUTH", "User not logged in.");
            //do nothing here
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
                      .setIsSmartLockEnabled(false)
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
                    //read from database and change view depending on the farmer boolean
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/users/" + user.getUid());
                    dbRef.addListenerForSingleValueEvent(new ValueEventListener(){
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //change view based on the received data
                            User user = dataSnapshot.getValue(User.class);
                            changeUserView(user);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { Log.d("AUTH", "User database read cancelled."); }
                    });
                }
            } else {
                //sign in failed because request was cancelled
                if(response == null) {
                    Log.d("AUTH", "Sign-In Cancelled.");
                    return;
                }

                //sign in failed because there was no network connection
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

    //changes the view based on the passed in user object
    public void changeUserView(User user) {
        //check if user is null
        if(user != null && FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.d("AUTH", "User <" + user.getUid() + "> " + FirebaseAuth.getInstance().getCurrentUser().getUid());
            if(user.getFarmer()) {
                //go to farmer view
                Intent intent = new Intent(this, FarmView.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("Current User", user);
                intent.putExtras(bundle);

                startActivity(intent);
                finish();
            } else {
                //go to vet view
                Intent intent = new Intent(this, VetView.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("Current User", user);
                intent.putExtras(bundle);

                startActivity(intent);
                finish();
            }
        } else {
            //problem finding authenticated user in database
            Log.d("AUTH", "ERROR: user not found <" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ">" );
            FirebaseAuth.getInstance().signOut(); //make sure the problematic authenticated user is signed out
        }
    }
}
