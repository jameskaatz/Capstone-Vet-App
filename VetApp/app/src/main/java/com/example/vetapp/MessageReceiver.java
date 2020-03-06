package com.example.vetapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class MessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages;
        String str = "";

        //Log.d("MSG_RECEIVE", "message receiver received message");

        if(bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            messages = new SmsMessage[pdus != null ? pdus.length : 0];
            for(int i = 0; i < messages.length; i++) {
                //this loop won't run if the pdus list is empty so we don't need to worry about puds[i] being null
                // I'm pretty sure
                messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                str += messages[i].getOriginatingAddress();
                str += ": ";
                str += messages[i].getMessageBody();
                str += "\n";
            }

            //Log.d("RESULTS",  str);

            //send a broadcast intent
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("SMS_RECEIVED_ACTION");
            broadcastIntent.putExtra("message", str);
            context.sendBroadcast(broadcastIntent);
        }
    }
}
