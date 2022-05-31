package com.homework.conference.service.exception;

public class InvalidTalkException extends RuntimeException {
    public InvalidTalkException() {
        super();
    }

    public InvalidTalkException(String message) {
        super(message);
    }
}
