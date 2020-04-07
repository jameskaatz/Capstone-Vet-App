package com.example.vetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class VetView extends AppCompatActivity {

    private Button createConvoBtn;

    private User currentUser;
    private ArrayList<Conversation> conversations;

    private RecyclerView convoList;
    private RecyclerView.Adapter convoListAdapter;
    private RecyclerView.LayoutManager convoListLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_view);

        //get the current user from the intent
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        currentUser = (User) bundle.getSerializable("Current User");

        createConvoBtn = findViewById(R.id.new_message);
        createConvoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                goToNewConversationActivity(currentUser);
            }
        });

        final ArrayList<String> conversationIds = currentUser.getConversationList();
        conversations = new ArrayList<Conversation>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/conversations");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Conversation c = ds.getValue(Conversation.class);
                    if(conversationIds.contains(c.getUid())){
                        Log.d("Get Conversations", "Found Conversation : " + c.getUid());
                        conversations.add(c);
                    }
                }
                initializeRecyclerView(conversations, currentUser);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Conversations", "Conversation query cancelled.");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vet_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //handle presses on the action bar items
        switch(item.getItemId()) {
            //this is essentially a "back" button that returns to the parent activity set in the manifest
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            //logout option
            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();
                goToLoginActivity();
                return true;

            //add contact option
            case R.id.menu_search:
                goToAddContactActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToLoginActivity() { startActivity(new Intent(this, LoginActivity.class)); }
    private void goToAddContactActivity(){
        Intent intent = new Intent(this, AddContactActivity.class);

        //pass user into next activity
        Bundle bundle = new Bundle();
        bundle.putSerializable("Current User", currentUser);
        intent.putExtras(bundle);

        startActivity(intent);
    }
    private void goToNewConversationActivity(User user){
        Intent intent = new Intent(this, NewConversationActivity.class);

        //pass user into next activity
        Bundle bundle = new Bundle();
        bundle.putSerializable("Current User", user);
        intent.putExtras(bundle);

        Log.d("Vet View", "Going into new conversation activity");
        startActivity(intent);
    }

    private void initializeRecyclerView(ArrayList<Conversation> conversations, User me) {
        convoList = findViewById(R.id.convoList);
        convoList.setNestedScrollingEnabled(false);
        convoList.setHasFixedSize(false);
        convoListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        convoList.setLayoutManager(convoListLayoutManager);

        convoListAdapter = new ConversationListAdapter(conversations, me, this);
        convoList.setAdapter(convoListAdapter);
    }

    //TODO add listeners to always listen for messages and conversations updates
    //  listener on the user's conversation list
    //  listener on all of the user's conversations << maybe this one is generated in ConversationListAdapter
}
