package com.homework.conference.exception;

public class InvalidTalkException extends RuntimeException {
    public InvalidTalkException() {
        super();
    }

    public InvalidTalkException(String message) {
        super(message);
    }
}
