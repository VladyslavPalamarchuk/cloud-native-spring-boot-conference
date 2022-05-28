package com.homework.conference.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidConferenceException extends RuntimeException {
    public InvalidConferenceException() {
        super();
    }

    public InvalidConferenceException(String message) {
        super(message);
    }
}
