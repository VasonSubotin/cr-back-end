package com.sm.model;

import java.io.Serializable;
import java.util.Date;

public class ServiceResult implements Serializable {
//    {
//        "timestamp": "2020-06-26T09:21:38.036+0000",
//            "status": 500,
//            "error": "Internal Server Error",
//            "message": "User test3 already exists!",
//            "path": "/signup"
//    }

    private Date timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ServiceResult() {
    }

    public ServiceResult(int status, String error, String message, String path) {
        this.timestamp = new Date();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
