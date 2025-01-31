package com.cathalob.medtracker.exception.validation;

import java.util.List;

public class UserModelValidationException extends ValidatorException {
    public UserModelValidationException(List<String> errors) {
        super(errors);
    }
}
