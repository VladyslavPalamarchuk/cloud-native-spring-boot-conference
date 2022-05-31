package com.homework.conference.service.exception;

public class InvalidConferenceException extends RuntimeException {
    public InvalidConferenceException() {
        super();
    }

    public InvalidConferenceException(String message) {
        super(message);
    }
}
