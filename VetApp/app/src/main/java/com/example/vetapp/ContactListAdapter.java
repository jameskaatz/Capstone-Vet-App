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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

class ContactListAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<ContactListAdapter.ContactListViewHolder> {

    private ArrayList<User> contactList;
    private User me;
    private Context parentContext;

    public ContactListAdapter(ArrayList<User> contacts, User user, Context packageContext) {
        contactList = contacts;
        me = user;
        parentContext = packageContext; //for intent
    }

    @NonNull
    @Override
    public ContactListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(lp);

        ContactListViewHolder clvh = new ContactListViewHolder(itemView);
        return clvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactListViewHolder holder, int position) {
        if(contactList.get(position).getName() != null) {
            holder.name.setText(contactList.get(position).getName());
        } else {
            holder.name.setText("Anonymous");
        }
        if(contactList.get(position).getPhone() != null) {
            holder.phone.setText("Phone: " + contactList.get(position).getPhone());
        } else {
            holder.phone.setText("Phone: Unknown");
        }
        if(contactList.get(position).getEmail() != null) {
            holder.email.setText("Email: " + contactList.get(position).getEmail());
        } else {
            holder.email.setText("Email: Unknown");
        }
        holder.user = contactList.get(position);
        holder.select.setText("Select");
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ContactListViewHolder extends RecyclerView.ViewHolder {

        public User user;
        public TextView name, phone, email;
        public Button select;
        public LinearLayout wholeItem;

        public ContactListViewHolder(@NonNull View view) {
            super(view);

            name = view.findViewById(R.id.name);
            phone = view.findViewById(R.id.phone);
            email = view.findViewById(R.id.email);

            wholeItem = view.findViewById(R.id.wholeItem);
            wholeItem.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //create the conversation and add to each user's coversation list
                    Log.d("Contacts", "Selected : " + user.getUid());

                    //get the reference for the new conversation
                    DatabaseReference ncRef = FirebaseDatabase.getInstance().getReference("/conversations").push();//client-side generates a new unique id
                    String uid = ncRef.getKey();
                    Conversation newConvo = new Conversation(uid, me.getUid(), user.getUid());

                    //TODO change this to go to the create symptom report activity
                    //  make sure to pass in all the information below and pass it on again when you go from the report page to the conversation page
                    //go to conversation activity with conversation in the bundle
                    Intent intent = new Intent(parentContext, ConversationActivity.class);

                    //pass user into next activity
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Current User", me);
                    bundle.putSerializable("Other User", user);
                    bundle.putSerializable("Conversation", newConvo);
                    intent.putExtras(bundle);

                    parentContext.startActivity(intent);
                }
            });
        }
    }
}
