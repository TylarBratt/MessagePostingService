package com.example.demo.dtos;

import java.util.UUID;

public class Roles {
    private UUID roleId;
    private String role;
    private String description;

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static String getAdmin() {
        return ADMIN;
    }

    public static String getProducer() {
        return PRODUCER;
    }

    public static String getSubscriber() {
        return SUBSCRIBER;
    }

    public static final String ADMIN = "ADMIN";
    public static final String PRODUCER = "PRODUCER";
    public static final String SUBSCRIBER = "SUBSCRIBER";
}
