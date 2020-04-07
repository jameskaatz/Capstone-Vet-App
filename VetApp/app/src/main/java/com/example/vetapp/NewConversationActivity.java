package com.example.vetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class NewConversationActivity extends AppCompatActivity {

    private User user;
    private ArrayList<User> full_contacts;
    private ArrayList<User> contacts;

    private EditText contactSearch;

    private RecyclerView contactList;
    private RecyclerView.Adapter contactListAdapter;
    private RecyclerView.LayoutManager contactListLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conversation);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        user = (User) bundle.getSerializable("Current User");

        contacts = new ArrayList<User>();

        contactSearch = findViewById(R.id.contact_search);
        contactSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //filter out the contacts (this can be optimized for sure)
                contacts.clear();
                for(User c : full_contacts) {
                    if(c.getName().contains(s))
                        contacts.add(c);
                }
                //update the recycler view
                contactListAdapter.notifyDataSetChanged();
            }
        });

        //get contact list
        final ArrayList<String> contactIds = user.getContactList();
        full_contacts = new ArrayList<User>();

        //this is the not cool way where we just snag what we need instead of the whole users list
        //but the count down latch thing was hanging up for some reason
        //I easily could have just missed the .countDown() call
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    User u = ds.getValue(User.class);
                    if(contactIds.contains(u.getUid())){
                        Log.d("Get Contacts", "Found Contact : " + u.getUid());
                        full_contacts.add(u);
                    }
                }
                contacts.addAll(full_contacts);
                initializeRecyclerView();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Contact Search", "Contact query cancelled.");
            }
        });
    }

    private void initializeRecyclerView() {
        contactList = findViewById(R.id.contactList);
        contactList.setNestedScrollingEnabled(false);
        contactList.setHasFixedSize(false);
        contactListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        contactList.setLayoutManager(contactListLayoutManager);

        contactListAdapter = new ContactListAdapter(contacts, user, this);
        contactList.setAdapter(contactListAdapter);
    }
}
