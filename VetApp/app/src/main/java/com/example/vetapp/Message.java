package com.example.vetapp;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Message implements Serializable {

    private String uid;
    private String sender_id;
    private String content;
    private Long timestamp;

    public Message(){}
    public Message(String mid){}
    public Message(String mid, String s_uid, String c, long ts) {
        content = c;
        sender_id = s_uid;
        uid = mid;
        timestamp = ts;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getSender() { return sender_id; }
    public void setSender(String sender_id) { this.sender_id = sender_id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}
