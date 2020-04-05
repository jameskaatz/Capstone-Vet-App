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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

class UserListAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {

    private ArrayList<User> userList;
    private User me;
    private DatabaseReference dbRef;

    public UserListAdapter(ArrayList<User> userList, User user) {
        //get our user info
        dbRef = FirebaseDatabase.getInstance().getReference("/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
        me = user;

        //set user list
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(lp);

        UserListViewHolder ulvh = new UserListViewHolder(itemView);
        return ulvh;
    }

    @Override
    public void onBindViewHolder(@NonNull UserListAdapter.UserListViewHolder holder, int position) {
        if(userList.get(position).getName() != null) {
            holder.name.setText(userList.get(position).getName());
        } else {
            holder.name.setText("Anonymous");
        }
        if(userList.get(position).getPhone() != null) {
            holder.phone.setText("Phone: " + userList.get(position).getPhone());
        } else {
            holder.phone.setText("Phone: Unknown");
        }
        if(userList.get(position).getEmail() != null) {
            holder.email.setText("Email: " + userList.get(position).getEmail());
        } else {
            holder.email.setText("Email: Unknown");
        }
        holder.user = userList.get(position);
        //if we already have the user as a contact then disable the add button
        if(me.hasContact(holder.user.getUid())) {
            holder.add.setEnabled(false);
            holder.add.setText("Added");
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserListViewHolder extends RecyclerView.ViewHolder
    {
        public User user;
        public TextView name, phone, email;
        public Button add;

        public UserListViewHolder(View view)
        {
            super(view);
            name = view.findViewById(R.id.name);
            phone = view.findViewById(R.id.phone);
            email = view.findViewById(R.id.email);

            add = view.findViewById(R.id.addBtn);
            add.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //add uid of new contact to contact list
                    me.addContact(user.getUid());
                    //write info back
                    dbRef.setValue(me)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Add Contact", "Adding contact " + user.getUid() + " to " + me.getUid() + "'s contacts successful");
                                    add.setEnabled(false);
                                    add.setText("Added");
                                    Log.d("Add Contact", me.getContactList().toString());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Add Contact", "Failed adding contact " + user.getUid() + " to " + me.getUid() + "'s contacts");
                                }
                            });
                }
            });
        }
    }
}
