package com.techzen.techsale.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



@SuppressWarnings("serial")
public class JsonResponse implements Serializable {

    private static final String OK = "ok";
    private static final String ERROR = "error";
    private Meta meta;
    private Object data;
    private List<ValidateError> validateErrorList = new ArrayList<>();

    public JsonResponse success() {
        this.meta = new Meta(true, OK);
        return this;
    }

    public JsonResponse success(Object data) {
        this.meta = new Meta(true, OK);
        this.data = data;
        return this;
    }

    public JsonResponse success(ResultVo result, Object data) {
        this.meta = new Meta(result.isSuccess(), result.getMessage());
        this.data = data;
        return this;
    }

    public JsonResponse failure() {
        this.meta = new Meta(false, ERROR);
        return this;
    }

    public JsonResponse failure(Object data, String message) {
        this.data = data;
        this.meta = new Meta(false, message);
        return this;
    }

    public JsonResponse failure(String message) {
        this.meta = new Meta(false, message);
        return this;
    }

    public JsonResponse failure(String message, List<ValidateError> validateErrorList) {
        this.meta = new Meta(false, message);
        this.validateErrorList = validateErrorList;
        return this;
    }

    public Meta getMeta() {
        return meta;
    }

    public Object getData() {
        return data;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public List<ValidateError> getValidateErrorList() {
        return validateErrorList;
    }

    public void setValidateErrorList(List<ValidateError> validateErrorList) {
        this.validateErrorList = validateErrorList;
    }
}

