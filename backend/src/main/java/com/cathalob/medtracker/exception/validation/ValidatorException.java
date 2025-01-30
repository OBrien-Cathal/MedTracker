package com.cathalob.medtracker.exception.validation;


import lombok.Getter;

import java.util.List;
@Getter

public class ValidatorException extends RuntimeException {

    private final List<String> errors;

    public ValidatorException(List<String> errors) {
        super("Validation Failed");
        this.errors = errors;
    }
}
