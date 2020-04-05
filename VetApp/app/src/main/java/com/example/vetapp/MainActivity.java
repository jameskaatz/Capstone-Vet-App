package com.example.vetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity{
    private Button buttonToFarm;
    private Button buttonToVet;
    private EditText displayName;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        displayName = (EditText) findViewById(R.id.displayName);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null && user.getDisplayName() != null) {
            displayName.setText(user.getDisplayName());
        }

        buttonToFarm = (Button) findViewById(R.id.button3);
        buttonToFarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null) {
                    //if the user is logged in only a new user would get to this point
                    //so we write new user data to the database
                    writeNewUser(user.getUid(),
                                 displayName.getText().toString(),
                                 user.getPhoneNumber(),
                                 user.getEmail(),
                                 true);
                    openFarmView();
                }
            }
        });

        buttonToVet = (Button) findViewById(R.id.button2);
        buttonToVet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null) {
                    //if the user is logged in only a new user would get to this point
                    //so we write new user data to the database
                    writeNewUser(user.getUid(),
                            displayName.getText().toString(),
                            user.getPhoneNumber(),
                            user.getEmail(),
                            false);
                    openVetView();
                }
            }
        });
    }

    public void openVetView(){
        //start up VetView activity
        Intent intent = new Intent(this, VetView.class);
        startActivity(intent);
    }

    public void openFarmView(){
        //start up FarmView activity
        Intent intent = new Intent(this, FarmView.class);
        startActivity(intent);
    }

    public void writeNewUser(String uid, String name, String phone, String email, boolean farmer) {
        //create user object
        User user = new User(uid, name, phone, email, farmer);

        //write user object to the database
        mDatabase.child("users").child(uid).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("AUTH", "User data written to database.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("AUTH", "User data write FAILED!");
                        Log.d("AUTH", e.getMessage());
                    }
                });
    }
}
