package com.example.vetapp;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User implements Serializable {

    private String uid, name, phone, email;
    private boolean farmer = true;//if false the user is a veterinarian
    private ArrayList<String> contactList;
    private ArrayList<String> conversationList;

    public User(){}

    public User(String uid) {
        this.uid = uid;
    }

    public User(String uid, String name, String phone, String email, boolean farmer) {
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.farmer = farmer;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public boolean getFarmer() {
        return farmer;
    }

    public ArrayList<String> getContactList(){
        if(contactList == null)
            contactList = new ArrayList<String>();
        return contactList;
    }

    public ArrayList<String> getConversationList() {
        if(conversationList == null)
            conversationList = new ArrayList<String>();
        return conversationList;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addContact(String contactUid) {
        if(contactList == null)
            contactList = new ArrayList<String>();
        if(!contactList.contains(contactUid))
            contactList.add(contactUid);
    }

    public boolean hasContact(String uid) { return contactList.contains(uid); }

    public void addConversation(String conversationUid) {
        if(conversationList == null)
            conversationList = new ArrayList<String>();
        if(!conversationList.contains(conversationUid))
            conversationList.add(conversationUid);
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("uid", uid);
        result.put("name", name);
        result.put("phone", phone);
        result.put("email", email);
        result.put("farmer", new Boolean(farmer));
        result.put("contactList", contactList);
        result.put("conversationList", conversationList);

        return result;
    }

    public boolean equals(Object o) {
        if(!(o instanceof User)) return false;
        //we only need to check if the uid's are the same cause it is unique for each user
        return uid.equals(((User) o).getUid());
    }
}
