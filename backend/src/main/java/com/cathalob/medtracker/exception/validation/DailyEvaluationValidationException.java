package com.cathalob.medtracker.exception.validation;

import java.util.List;

public class DailyEvaluationValidationException extends ValidatorException{

    public DailyEvaluationValidationException(List<String> errors) {
        super(errors);
    }
}
