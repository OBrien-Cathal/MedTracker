package com.cathalob.medtracker.exception.validation;

import java.util.List;

public class PatientRegistrationException extends ValidatorException{


    public PatientRegistrationException(List<String> errors) {
        super(errors);
    }
}
