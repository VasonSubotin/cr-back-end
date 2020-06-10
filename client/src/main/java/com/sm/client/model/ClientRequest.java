package com.sm.client.model;

import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;

public class ClientRequest implements Serializable {
   private String clientId;
   private String clientSecret;

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
