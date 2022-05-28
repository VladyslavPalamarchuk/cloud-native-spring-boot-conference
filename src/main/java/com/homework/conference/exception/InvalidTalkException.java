package com.homework.conference.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidTalkException extends RuntimeException {
    public InvalidTalkException() {
        super();
    }

    public InvalidTalkException(String message) {
        super(message);
    }
}
