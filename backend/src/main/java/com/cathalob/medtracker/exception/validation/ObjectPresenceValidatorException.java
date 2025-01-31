package com.cathalob.medtracker.exception.validation;

import java.util.List;

public class ObjectPresenceValidatorException extends ValidatorException{

    public ObjectPresenceValidatorException(List<String> errors) {
        super(errors);
    }
}
