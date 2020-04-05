package com.example.vetapp;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class Conversation {

    private String uid, member_a, member_b;
    private ArrayList<String> messages;

    public Conversation(){}
    public Conversation(String uid){}
    public Conversation(String convo_uid, String a_uid, String b_uid) {
        uid = convo_uid;
        member_a = a_uid;
        member_b = b_uid;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getMemberA() { return member_a; }
    public void setMemberA(String member_a) { this.member_a = member_a; }

    public String getMemberB() { return member_b; }
    public void setMemberB(String member_b) { this.member_b = member_b; }

    public ArrayList<String> getMessages(){ return messages; }
    public void addMessage(String uid){
        if(messages == null)
            messages = new ArrayList<String>();
        if(!messages.contains(uid)) //we probably won't need this
            messages.add(uid);
    }
}
