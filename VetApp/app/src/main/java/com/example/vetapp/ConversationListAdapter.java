package com.example.vetapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

    public ConversationListAdapter(ArrayList<Conversation> conversations, User user) {
        convoList = conversations;
        me = user;
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

        //grab the user from the conversation
        dbRef.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                holder.memberB = user;
                holder.name.setText(user.getUid());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { Log.d("Binding Conversations", "Convesation member search cancelled"); }
        });

        //grab the latest message for the message preview
        holder.messagePreview.setText("Implement message preview");
    }

    @Override
    public int getItemCount() {
        return convoList.size();
    }

    public class ConversationListViewHolder extends RecyclerView.ViewHolder
    {
        public User memberA;
        public User memberB;
        public TextView name;
        public TextView messagePreview;

        public ConversationListViewHolder(View view)
        {
            super(view);
            name = view.findViewById(R.id.name);
            messagePreview = view.findViewById(R.id.preview);
        }
    }
}
