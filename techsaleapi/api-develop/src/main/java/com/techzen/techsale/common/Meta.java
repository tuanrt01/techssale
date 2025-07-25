package com.techzen.techsale.common;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Meta implements Serializable {

    private boolean success;
    private String message;

    public Meta() {

    }

    public Meta(boolean success) {
        this.success = success;
    }

    public Meta(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
