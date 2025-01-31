package com.cathalob.medtracker.exception.validation;

import java.util.List;

public class PrescriptionValidatorException extends ValidatorException{
    public PrescriptionValidatorException(List<String> errors) {
        super(errors);
    }
}
