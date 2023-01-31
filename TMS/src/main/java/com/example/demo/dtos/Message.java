package com.example.demo.dtos;

import java.util.Date;
import java.util.UUID;

public class Message {
    private UUID id;
    private UUID author;
    private String content;
    private Date timestamp;

    public UUID getId() {
        return id;
    }

    public Message(UUID id2, UUID userID, String content, Date date) {
        this.id = id2;
        this.author = userID;
        this.content = content;
        this.timestamp = date;
    }

    public Message() {

        this.id = null;
        this.author = null;
        this.content = null;
        this.timestamp = new Date();
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAuthor() {
        return author;
    }

    public void setAuthor(UUID author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
