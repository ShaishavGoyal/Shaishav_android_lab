package com.example.shaishavandroidlab;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ChatMessage {
    @ColumnInfo(name = "message")
    String message;
    @ColumnInfo(name = "timeSent")
    String timeSent;
    @ColumnInfo(name = "SendOrReceive")
    public int sendOrReceive;
    boolean isSentButton;
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;
    public ChatMessage(String m, String t, boolean sent)
    {
        this.message = m;
        this.timeSent = t;
        this.isSentButton = sent;
    }
    public ChatMessage(){
    }
    public String getMessage() {
        return message;
    }

    public String getTimeSent() {
        return timeSent;
    }

    public boolean IsSentButton(){
        return isSentButton;
    }
}
