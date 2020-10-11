package com.sm.client.model;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class JwtTokenRequest implements Serializable {
    private LinkedHashMap<String,Object> header;
    private LinkedHashMap<String,Object> payload;

    public LinkedHashMap<String, Object> getHeader() {
        return header;
    }

    public void setHeader(LinkedHashMap<String, Object> header) {
        this.header = header;
    }

    public LinkedHashMap<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(LinkedHashMap<String, Object> payload) {
        this.payload = payload;
    }
}
