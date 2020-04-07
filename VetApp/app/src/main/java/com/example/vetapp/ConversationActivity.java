package com.example.vetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CountDownLatch;

public class ConversationActivity extends AppCompatActivity {

    private User currentUser;
    private User otherUser;
    private Conversation conversation;
    private ArrayList<Message> messages;

    private Button sendButton;
    private EditText messageText;

    private RecyclerView messageList;
    private RecyclerView.Adapter messageListAdapter;
    private RecyclerView.LayoutManager messageListLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        //get the current user from the intent
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        currentUser = (User) bundle.getSerializable("Current User");
        otherUser = (User) bundle.getSerializable("Other User");
        conversation = (Conversation) bundle.getSerializable("Conversation");

        //gather messages and init recycler view
        final ArrayList<String> messageIds = conversation.getMessages();
        messages = new ArrayList<Message>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/messages");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Message m = ds.getValue(Message.class);
                    if(messageIds.contains(m.getUid())){
                        messages.add(m);
                    }
                }

                //sort by the timestamp since the messages could come in out of order
                Collections.sort(messages, new Comparator<Message>() {
                    @Override
                    public int compare(Message o1, Message o2) {
                        Long val1 = o1.getTimestamp();
                        Long val2 = o2.getTimestamp();
                        return val1.compareTo(val2);
                    }
                });

                //initialize recycler view
                initializeRecyclerView(messages, currentUser);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { Log.d("Conversation", "Messages query cancelled."); }
        });

        messageText = findViewById(R.id.messageText);
        sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing if there is nothing in the edit text
                if(messageText.getText().toString().equals(""))
                    return;

                //get message database reference
                DatabaseReference ncRef = FirebaseDatabase.getInstance().getReference("/messages").push();//client-side generates a new unique id
                String uid = ncRef.getKey();
                Message m = new Message(uid, currentUser.getUid(), messageText.getText().toString(), System.currentTimeMillis());
                messages.add(m);
                ncRef.setValue(m)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Conversation", "Message data written to database");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Conversation", "Message data failed to write to database");
                                Log.d("Conversation", e.getMessage());
                            }
                        });

                //update the conversation
                conversation.addMessage(m.getUid());
                DatabaseReference cRef = FirebaseDatabase.getInstance().getReference("/conversations/" + conversation.getUid());
                cRef.setValue(conversation)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Conversation", "Conversation data written to database");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Conversation", "Convesation data failed to write to database");
                                Log.d("Conversation", e.getMessage());
                            }
                        });

                //if the conversation had a message list of length zero then update both user's conversation list
                //    add to beginning
                if(conversation.getMessages().size() == 1)//one since we just added this new message
                {
                    //update current user's database stuff
                    DatabaseReference cuRef = FirebaseDatabase.getInstance().getReference("/users/" + currentUser.getUid());
                    currentUser.addConversation(conversation.getUid());
                    cuRef.setValue(currentUser)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Conversation", "Current user data written to database");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Conversation", "Current user data failed to write to database");
                                    Log.d("Conversation", e.getMessage());
                                }
                            });

                    //update other user's database stuff
                    DatabaseReference ouRef = FirebaseDatabase.getInstance().getReference("/users/" + otherUser.getUid());
                    otherUser.addConversation(conversation.getUid());
                    ouRef.setValue(otherUser)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Conversation", "Other user data written to database");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Conversation", "Other user data failed to write to database");
                                    Log.d("Conversation", e.getMessage());
                                }
                            });
                }

                //reset the edit text field
                messageText.setText("");
                //update the recycler view
                messageListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initializeRecyclerView(ArrayList<Message> messages, User me) {
        messageList = findViewById(R.id.messageList);
        messageList.setNestedScrollingEnabled(false);
        messageList.setHasFixedSize(false);
        messageListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        messageList.setLayoutManager(messageListLayoutManager);

        messageListAdapter = new MessageListAdapter(messages, me);
        messageList.setAdapter(messageListAdapter);
    }

    //TODO return to correct vet versus farmer view (There is a way to do this programmatically)
    //  I think this can be done by making a menu and on the back option just start a new activity of parentContext
    //  We also have the current user here
    //  but we want to change the parent view so the regular "back" button on android phones works correctly
}
