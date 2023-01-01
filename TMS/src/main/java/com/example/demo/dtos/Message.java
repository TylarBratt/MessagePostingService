package com.example.demo.dtos;

import java.util.UUID;

public class Message {
    private UUID id;
    private UUID author;
    private String content;
    private long timestamp;

    public UUID getId() {
        return id;
    }

    public Message(UUID id, UUID author, String content, long timestamp) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.timestamp = timestamp;
    }

    public Message() {

        this.id = null;
        this.author = null;
        this.content = null;
        this.timestamp = 0;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
