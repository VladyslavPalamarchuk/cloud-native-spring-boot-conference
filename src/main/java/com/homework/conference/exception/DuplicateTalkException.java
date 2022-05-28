package com.homework.conference.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class DuplicateTalkException extends RuntimeException {
    public DuplicateTalkException() {
        super();
    }
}
