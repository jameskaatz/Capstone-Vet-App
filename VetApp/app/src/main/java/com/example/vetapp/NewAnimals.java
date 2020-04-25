package com.example.vetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;

public class NewAnimals extends AppCompatActivity {

    private User currentUser;
    private DatabaseReference userRef;

    private Button moreSympBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_animals);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        currentUser = (User) bundle.getSerializable("Current User");

        Spinner animalSpinner = (Spinner) findViewById(R.id.setAnimalSpinner);

        ArrayAdapter<String> adapterAnimals = new ArrayAdapter<String>(NewAnimals.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.animalsList));

        adapterAnimals.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        animalSpinner.setAdapter(adapterAnimals);

        Spinner sexSpinner = (Spinner) findViewById(R.id.setSexSpinner);

        ArrayAdapter<String> adapterSex = new ArrayAdapter<String>(NewAnimals.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.animalSex));

        adapterSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexSpinner.setAdapter(adapterSex);

        moreSympBtn = (Button) findViewById(R.id.button5);
        moreSympBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMoreSymp(currentUser);
            }
        });
    }

    private void goToMoreSymp(User currentUser){
        Intent intent = new Intent(this, MoreSymp.class);

        //pass user into next activity
        Bundle bundle = new Bundle();
        bundle.putSerializable("Current User", currentUser);
        intent.putExtras(bundle);

        startActivity(intent);
    }
}
