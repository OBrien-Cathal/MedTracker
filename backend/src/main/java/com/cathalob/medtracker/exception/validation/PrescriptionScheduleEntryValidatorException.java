package com.cathalob.medtracker.exception.validation;

import java.util.List;

public class PrescriptionScheduleEntryValidatorException extends ValidatorException{
    public PrescriptionScheduleEntryValidatorException(List<String> errors) {
        super(errors);
    }
}
