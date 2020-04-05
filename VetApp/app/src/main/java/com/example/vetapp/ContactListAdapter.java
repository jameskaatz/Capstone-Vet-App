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

import java.util.ArrayList;

class ContactListAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<ContactListAdapter.ContactListViewHolder> {

    private ArrayList<User> contactList;
    private User me;

    public ContactListAdapter(ArrayList<User> contacts, User user) {
        contactList = contacts;
        me = user;
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

        public ContactListViewHolder(@NonNull View view) {
            super(view);

            name = view.findViewById(R.id.name);
            phone = view.findViewById(R.id.phone);
            email = view.findViewById(R.id.email);

            select = view.findViewById(R.id.addBtn);//this naming is off cause we are using the user_list_item
            select.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //create the conversation and add to each user's coversation list
                    Log.d("Contacts", "Selected : " + user.getUid());
                    //TODO create a conversation then go to conversation
                }
            });
        }
    }
}
