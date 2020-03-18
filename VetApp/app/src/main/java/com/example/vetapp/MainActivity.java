package com.example.vetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{
    private Button buttonToFarm;
    private Button buttonToVet;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonToFarm = (Button) findViewById(R.id.button3);
        buttonToFarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFarmView();
            }
        });

        buttonToVet = (Button) findViewById(R.id.button2);
        buttonToVet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVetView();
            }
        });
    }

    public void openVetView(){
        //TODO: Database writing here
        Intent intent = new Intent(this, VetView.class);
        startActivity(intent);
    }

    public void openFarmView(){
        //TODO: Database writing here
        Intent intent = new Intent(this, FarmView.class);
        startActivity(intent);
    }
}
