package com.gwozdz1uuu.sggwstudentmap.user;

public class UserAlreadyExistsException extends RuntimeException {
    private final String fieldName;

    public UserAlreadyExistsException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}