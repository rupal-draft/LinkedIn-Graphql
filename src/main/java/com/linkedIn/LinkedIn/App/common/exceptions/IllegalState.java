package com.linkedIn.LinkedIn.App.common.exceptions;

import org.springframework.http.HttpStatus;

public class IllegalState extends BaseException {
    public IllegalState(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
