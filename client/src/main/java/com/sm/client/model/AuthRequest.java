package com.sm.client.model;

import java.io.Serializable;

public class AuthRequest implements Serializable {

    private String username;
    private String password;


    public AuthRequest() {
    }

    public AuthRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}