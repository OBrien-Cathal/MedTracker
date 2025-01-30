package com.cathalob.medtracker.validate;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Validator {
    private final List<String> errors;

    public Validator() {
        errors = new ArrayList<>();
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public boolean validationFailed() {
        return !isValid();
    }

    protected void addError(String error) {
        this.errors.add(error);
    }

    protected void addErrors(List<String> errors) {
        this.errors.addAll(errors);
    }


    protected void validateUsingSubValidator(Validator subValidator) {
        if (!subValidator.isValid()) {
            addErrors(subValidator.getErrors());
        }
    }

    protected boolean objectIsAbsent(Object object) {
        return object == null;
    }

}
