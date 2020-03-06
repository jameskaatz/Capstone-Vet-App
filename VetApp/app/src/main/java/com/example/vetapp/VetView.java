package com.example.vetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class VetView extends AppCompatActivity {

    private Button buttonSend;
    private EditText textMessage;
    private EditText phoneNumber;
    private IntentFilter intentFilter;

    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //push the new text message into the message list
            //Log.d("GOT HERE", "intent receiver received intent");
            //Log.d("MESSAGE RECEIVED", "Received: \"" + intent.getExtras().getString("message") + "\"");

            //TODO: message received here (needed for conversation implementation)
            TextView messages = (TextView) findViewById(R.id.message);
            messages.setText(intent.getExtras().getString("message"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_view);

        //set up intent filter
        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");

        buttonSend = (Button) findViewById(R.id.buttonSend);
        textMessage = (EditText) findViewById(R.id.textMessage);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this check is just in case something weird happens but I don't think is needed
                if(textMessage.getText() != null && phoneNumber.getText() != null) {
                    String message = textMessage.getText().toString();
                    String number = phoneNumber.getText().toString();

                    //TODO: message sent here (needed for conversation implementation)
                    sendMessage(number, message);
                }
            }
        });

    }

    protected void sendMessage(String phone, String message) {
        //check to see if we have a phone number and a message to actually send
        if(phone.equals("") || message.equals("")) {
            return; //don't do anything
        }
        String SENT = "Message Sent";
        String DELIVERED = "Message Delivered";

        PendingIntent piSent = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent piDelivered = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phone, null, message, piSent, piDelivered);
    }

    @Override
    protected void onResume(){
        registerReceiver(intentReceiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onPause(){
        unregisterReceiver(intentReceiver);
        super.onPause();
    }

}
