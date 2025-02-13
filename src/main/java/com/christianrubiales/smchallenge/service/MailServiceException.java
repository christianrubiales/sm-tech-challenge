package com.christianrubiales.smchallenge.service;

public class MailServiceException extends RuntimeException {

    public MailServiceException(String message) {
        super(message);
    }

    public MailServiceException(Exception e) {
        super(e);
    }
}
