package com.techzen.techsale.common;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ValidateError implements Serializable {

    private String objectName;
    private String field;
    private String value;
    private String validateMessage;

    public ValidateError() {

    }

    public ValidateError(String objectName, String field, String value, String validateMessage) {
        super();
        this.objectName = objectName;
        this.field = field;
        this.value = value;
        this.validateMessage = validateMessage;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValidateMessage() {
        return validateMessage;
    }

    public void setValidateMessage(String validateMessage) {
        this.validateMessage = validateMessage;
    }
}

