package com.cathalob.medtracker.validate;

import com.cathalob.medtracker.exception.validation.ObjectPresenceValidatorException;
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

    protected String objectNotPresentMessage(){
        return objectToValidateName();
    }

    protected String objectToValidateName() {
        return this.getClass().getSimpleName();
    }

    protected void validationObjectIsPresent(Object object) {
        if (object == null) {
            addError(objectToValidateName() + "Object to validate is missing");
            cannotContinueValidation();
        }
    }

    protected void validateObjectPresence(Object objectToValidate) {
        try {
            ObjectPresenceValidator.aObjectPresenceValidator(objectToValidate, objectNotPresentMessage()).validate();
        } catch (ObjectPresenceValidatorException e) {
            addErrors(e.getErrors());
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
