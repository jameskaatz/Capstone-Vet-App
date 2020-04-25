package com.example.vetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;

public class MoreSymp extends AppCompatActivity {

    private User currentUser;
    private DatabaseReference userRef;

    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        globalClass globalClass = (globalClass) getApplicationContext();

        String str = String.valueOf(globalClass);
        Log.d("AUTH", str);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_symp);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        currentUser = (User) bundle.getSerializable("Current User");

        submitBtn = (Button) findViewById(R.id.submitCaseBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retunrToFarmView(currentUser);
            }
        });

        Spinner bodyCondSpinner =  (Spinner) findViewById(R.id.bodyCondSpinner);

        ArrayAdapter<String> adapterCond = new ArrayAdapter<String>(MoreSymp.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.bodyConditionScore));

        adapterCond.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bodyCondSpinner.setAdapter(adapterCond);

        Spinner poopSpinner = (Spinner) findViewById(R.id.poopSpinner);

        ArrayAdapter<String> adapterPoop = new ArrayAdapter<String>(MoreSymp.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.animalPoop));

        adapterPoop.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        poopSpinner.setAdapter(adapterPoop);

        Spinner urineSpinner = (Spinner) findViewById(R.id.urineSpinner);

        ArrayAdapter<String> adapterUrine = new ArrayAdapter<String>(MoreSymp.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.animalUrine));

        adapterUrine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        urineSpinner.setAdapter(adapterUrine);

        Spinner eyeSpinner = (Spinner) findViewById(R.id.eyeSpinner);

        ArrayAdapter<String> adapterEye = new ArrayAdapter<String>(MoreSymp.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.eyeAnemiaScore));

        adapterEye.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eyeSpinner.setAdapter(adapterEye);

        Spinner gaitSpinner = (Spinner) findViewById(R.id.gaitSpinner);

        ArrayAdapter<String> adapterGait = new ArrayAdapter<String>(MoreSymp.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.animalMovement));

        adapterGait.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gaitSpinner.setAdapter(adapterGait);

        Spinner jawSpinner = (Spinner) findViewById(R.id.jawSpinner);

        ArrayAdapter<String> adapterJaw = new ArrayAdapter<String>(MoreSymp.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.yesNoNotsure));

        adapterJaw.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jawSpinner.setAdapter(adapterJaw);

        Spinner skinSpinner = (Spinner) findViewById(R.id.skinSpinner);

        ArrayAdapter<String> adapterSkin = new ArrayAdapter<String>(MoreSymp.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.animalSkin));

        adapterSkin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        skinSpinner.setAdapter(adapterSkin);

        Spinner woolSpinner = (Spinner) findViewById(R.id.woolSpinner);

        ArrayAdapter<String> adapterWool = new ArrayAdapter<String>(MoreSymp.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.animalWool));

        adapterWool.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        woolSpinner.setAdapter(adapterWool);

        Spinner appetiteSpinner = (Spinner) findViewById(R.id.appetiteSpinner);

        ArrayAdapter<String> adapterAppetite = new ArrayAdapter<String>(MoreSymp.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.animalAppetite));

        adapterAppetite.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        appetiteSpinner.setAdapter(adapterAppetite);

        Spinner cudSpinner = (Spinner) findViewById(R.id.cudSpinner);

        ArrayAdapter<String> adapterCud = new ArrayAdapter<String>(MoreSymp.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.yesNoNotsure));

        adapterCud.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cudSpinner.setAdapter(adapterCud);


    }
    private void retunrToFarmView(User currentUser){
        Intent intent = new Intent(this, FarmView.class);
        globalClass globalClass = (globalClass) getApplicationContext();
        globalClass.setOpenCase(true);

        //pass user into next activity
        Bundle bundle = new Bundle();
        bundle.putSerializable("Current User", currentUser);
        intent.putExtras(bundle);
        intent.putExtra("com.globalClass.EXTRA_TEXT", globalClass.getOpenCase());

        startActivity(intent);
    }
}
