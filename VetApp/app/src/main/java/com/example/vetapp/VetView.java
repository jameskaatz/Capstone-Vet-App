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

    private RecyclerView convoList;
    private RecyclerView.Adapter convoListAdapter;
    private RecyclerView.LayoutManager convoListLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_view);

        //get the current user from the data base
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        currentUser = (User) bundle.getSerializable("Current User");

        //get conversations
        ArrayList<String> conversationIds = currentUser.getConversationList();
        final ArrayList<Conversation> conversationList = new ArrayList<Conversation>();
        final CountDownLatch gotConvos = new CountDownLatch(conversationIds.size());
        for(String conversationId : conversationIds) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/conversations/" + conversationId);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    conversationList.add(dataSnapshot.getValue(Conversation.class));
                    gotConvos.countDown();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { Log.d("Vet View", "Conversation query cancelled."); }
            });
        }

        try {
            gotConvos.await(); //wait until firebase responds with all of the conversations
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        createConvoBtn = findViewById(R.id.new_message);
        createConvoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                goToNewConversationActivity(currentUser);
            }
        });

        initializeRecyclerView(conversationList, currentUser);
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

        convoListAdapter = new ConversationListAdapter(conversations, me);
        convoList.setAdapter(convoListAdapter);
    }
}
