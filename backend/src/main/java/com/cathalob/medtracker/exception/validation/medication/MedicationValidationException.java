package com.cathalob.medtracker.exception.validation.medication;


import com.cathalob.medtracker.exception.validation.ValidatorException;

import java.util.List;

public class MedicationValidationException extends ValidatorException {

    public MedicationValidationException(List<String> errors) {
        super(errors);
    }
}
