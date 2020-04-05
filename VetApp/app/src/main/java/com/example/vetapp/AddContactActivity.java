package com.example.vetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AddContactActivity extends AppCompatActivity {

    private EditText searchName;
    private EditText searchPhone;
    private EditText searchEmail;

    private Button search;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        currentUser = (User) bundle.getSerializable("Current User");

        searchName = (EditText)findViewById(R.id.name);
        searchPhone = (EditText)findViewById(R.id.phone);
        searchEmail = (EditText)findViewById(R.id.email);

        search = (Button)findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //get all the input and query the user database for matches
                if(!searchName.getText().toString().equals("") || !searchPhone.getText().toString().equals("") || !searchEmail.getText().toString().equals("")) {
                    //pass variables to next activity to do the database query
                    search(searchName.getText().toString(),
                            searchPhone.getText().toString(),
                            searchEmail.getText().toString());
                }else {
                    //do nothing or show an error message
                }
            }
        });
    }

    private void search(final String name, final String phone, final String email) {
        //use the passed strings to query the database. this method pulls all users and loops through them on client side
        //this could be optimized but working with such a small amount of users we don't need this to be fast
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/users");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<User> searchResults = new ArrayList<User>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    //we don't want to add ourselves as a contact
                    if(user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        continue;
                    }

                    if(user.getName() != null && user.getName().equals(name)) {
                        searchResults.add(user);
                    }
                    else if(user.getPhone() != null && user.getPhone().equals(phone)) {
                        searchResults.add(user);
                    }
                    else if(user.getEmail() != null && user.getEmail().equals(email)) {
                        searchResults.add(user);
                    }
                }
                //sort results by how much they match the search terms
                Collections.sort(searchResults, new Comparator<User>() {
                    @Override
                    public int compare(User o1, User o2) {
                        Integer val1 = (o1.getName() != null && o1.getName().equals(name)) ? 1 : 0;
                        val1 += (o1.getPhone() != null && o1.getPhone().equals(phone)) ? 1 : 0;
                        val1 += (o1.getEmail() != null && o1.getEmail().equals(email)) ? 1 : 0;

                        Integer val2 = (o2.getName() != null && o2.getName().equals(name)) ? 1 : 0;
                        val2 += (o2.getPhone() != null && o2.getPhone().equals(phone)) ? 1 : 0;
                        val2 += (o2.getEmail() != null && o2.getEmail().equals(email)) ? 1 : 0;

                        return val2.compareTo(val1);
                    }
                });

                resultsActivity(searchResults, currentUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { Log.d("User Search", "User database read cancelled."); }
        });


    }

    private void resultsActivity(ArrayList<User> results, User me) {
        //create intent
        Intent intent = new Intent(this, UserSearchActivity.class);

        //pass arraylist into next activity
        Bundle bundle = new Bundle();
        bundle.putSerializable("Results", results);
        bundle.putSerializable("Me", me);
        intent.putExtras(bundle);

        //start next activity
        startActivity(intent);
    }
}
