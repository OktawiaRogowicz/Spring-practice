package com.example.accessingdatamysql.exception.domain;

public class UserNotFoundException extends Exception {
    public UserNotFoundException (String message) {
        super(message);
    }
}
