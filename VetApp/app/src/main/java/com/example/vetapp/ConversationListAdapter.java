package com.example.vetapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class ConversationListAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<ConversationListAdapter.ConversationListViewHolder> {

    private ArrayList<Conversation> convoList;
    private User me;
    private DatabaseReference dbRef;
    private Context parentContext;

    public ConversationListAdapter(ArrayList<Conversation> conversations, User user, Context packageContext) {
        convoList = conversations;
        me = user;
        parentContext = packageContext;
    }

    @NonNull
    @Override
    public ConversationListAdapter.ConversationListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_list_item, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(lp);

        ConversationListViewHolder clvh = new ConversationListViewHolder(itemView);
        return clvh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ConversationListAdapter.ConversationListViewHolder holder, int position) {
        Conversation convo = convoList.get(position);

        if(convo.getMemberA().equals(me.getUid())) {
            //get user of uid getMemberB
            dbRef = FirebaseDatabase.getInstance().getReference("/users/" + convo.getMemberB());
        } else {
            //get user of uid getMemberA
            dbRef = FirebaseDatabase.getInstance().getReference("/users/" + convo.getMemberA());
        }

        holder.memberA = me; //always make member A the current logged in user
        holder.conversation = convo;

        //grab the user from the conversation
        dbRef.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                holder.memberB = user;
                holder.name.setText(user.getUid());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { Log.d("Binding Conversations", "Conversation member search cancelled"); }
        });

        //grab the latest message for the message preview
        String messageUid = convo.getLatestMessage();
        if(messageUid == null) {
            holder.messagePreview.setText("No messages.");
        } else {
            DatabaseReference lmref = FirebaseDatabase.getInstance().getReference("/messages/" + messageUid);
            lmref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Message message = dataSnapshot.getValue(Message.class);
                    holder.messagePreview.setText(message.getContent());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { Log.d("Binding Conversations", "Message preview request cancelled."); }
            });
        }
        holder.startConvoListener();
    }

    @Override
    public int getItemCount() {
        return convoList.size();
    }

    public class ConversationListViewHolder extends RecyclerView.ViewHolder
    {
        public User memberA;
        public User memberB;
        public Conversation conversation;
        public DatabaseReference convoRef;

        public TextView name;
        public TextView messagePreview;
        public LinearLayout wholeItem;

        public ConversationListViewHolder(View view)
        {
            super(view);
            name = view.findViewById(R.id.name);
            messagePreview = view.findViewById(R.id.preview);
            wholeItem = view.findViewById(R.id.conversationItem);
            wholeItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(parentContext, ConversationActivity.class);

                    //pass user, other user, and conversation into next activity
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Current User", memberA);
                    bundle.putSerializable("Other User", memberB);
                    bundle.putSerializable("Conversation", conversation);
                    intent.putExtras(bundle);

                    parentContext.startActivity(intent);
                }
            });
        }

        public void startConvoListener()
        {
            convoRef = FirebaseDatabase.getInstance().getReference("/conversations/" + conversation.getUid() + "/messages");
            convoRef.addChildEventListener(new ChildEventListener(){
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String messageId = dataSnapshot.getValue(String.class);
                    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("/messages/" + messageId);
                    mRef.addListenerForSingleValueEvent(new ValueEventListener(){
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Message latest = dataSnapshot.getValue(Message.class);
                            messagePreview.setText(latest.getContent());
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { Log.d("Conversation List", "Receive latest message cancelled."); }
                    });
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { Log.d("Conversation List", "Receive latest message id cancelled."); }
            });
        }
    }
}
