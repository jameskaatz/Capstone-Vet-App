package com.example.vetapp;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User implements Serializable {

    private String uid, name, phone, email;
    private boolean farmer = true;//if false the user is a veterinarian

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

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("uid", uid);
        result.put("name", name);
        result.put("phone", phone);
        result.put("email", email);
        result.put("farmer", new Boolean(farmer));

        return result;
    }

}
