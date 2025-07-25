package com.techzen.techsale.common;

public class ResultVo {

    private boolean success = true;
    private String message = "OK";

    public ResultVo() {

    }

    public ResultVo(boolean success) {
        this.success = success;
    }

    public ResultVo(boolean success, String message) {
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
        if (message == null || "".equals(message)) {
            success = false;
        }
        this.message = message;
    }
}

