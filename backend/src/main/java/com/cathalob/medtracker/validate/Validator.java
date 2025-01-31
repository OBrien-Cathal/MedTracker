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

    protected String objectToValidateName() {
        return this.getClass().getName();
    }

    protected void validationObjectIsPresent(Object object) {
        if (object == null) {
            addError(objectToValidateName() + "Object to validate is missing");
            cannotContinueValidation();
        }
    }

    protected void cannotContinueValidation() {
        raiseValidationException();
    }

    protected boolean objectIsAbsent(Object object) {
        return object == null;
    }

    protected abstract void basicValidate();

    protected abstract void raiseValidationException();

    public void validate() {
        basicValidate();
        if (validationFailed()) {
            raiseValidationException();
        }

    }
}
