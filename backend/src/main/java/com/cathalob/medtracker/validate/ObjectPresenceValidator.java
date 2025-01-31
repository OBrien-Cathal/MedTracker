package com.cathalob.medtracker.validate;

import com.cathalob.medtracker.exception.validation.ObjectPresenceValidatorException;

public class ObjectPresenceValidator extends Validator {
    private Object object;
    private final String validatorName;

    public ObjectPresenceValidator(Object object, String validatorName) {
        this.object = object;
        this.validatorName = validatorName;
    }

    @Override
    protected void basicValidate() {
        if (object == null) {
            addError(ObjectPresenceValidator.ObjectMissingErrorMessage(validatorName));
            cannotContinueValidation();
        }
    }

    @Override
    protected void raiseValidationException() {
        throw new ObjectPresenceValidatorException(getErrors());
    }


    public static String ObjectMissingErrorMessage(String validatorName) {
        return "Missing Validation Subject: " + validatorName;
    }

    public static ObjectPresenceValidator aObjectPresenceValidator(Object object, String validatorName) {
        return new ObjectPresenceValidator(object, validatorName);
    }

}
