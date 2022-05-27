package com.homework.conference.exception;

public class ConferenceNotFoundException extends RuntimeException {
    public ConferenceNotFoundException() {
        super();
    }

    public ConferenceNotFoundException(String message) {
        super(message);
    }
}
