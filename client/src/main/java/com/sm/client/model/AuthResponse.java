package com.sm.client.model;

import java.io.Serializable;

public class AuthResponse implements Serializable {

    private final String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}