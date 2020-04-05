package com.example.vetapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class UserSearchActivity extends AppCompatActivity {

    private ArrayList<User> searchResults;
    private User currentUser;
    private Button button;

    private RecyclerView userList;
    private RecyclerView.Adapter userListAdapter;
    private RecyclerView.LayoutManager userListLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        button = findViewById(R.id.done);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                goToVetView();
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        searchResults = (ArrayList<User>) bundle.getSerializable("Results");
        currentUser = (User) bundle.getSerializable("Me");

        initializeRecyclerView();
    }

    private void goToVetView()
    {
        Intent intent = new Intent(this, VetView.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("Current User", currentUser);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    private void initializeRecyclerView() {
        userList = findViewById(R.id.userList);
        userList.setNestedScrollingEnabled(false);
        userList.setHasFixedSize(false);
        userListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        userList.setLayoutManager(userListLayoutManager);

        userListAdapter = new UserListAdapter(searchResults, currentUser);
        userList.setAdapter(userListAdapter);
    }
}
