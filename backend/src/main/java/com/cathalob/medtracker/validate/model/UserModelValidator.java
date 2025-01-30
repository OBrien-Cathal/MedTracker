package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.model.UserModel;

import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.validate.Validator;
import com.cathalob.medtracker.validate.model.errors.UserModelError;

public class UserModelValidator extends Validator {
    private final UserModel userModel;

    public UserModelValidator(UserModel userModel) {
        this.userModel = userModel;
    }

    private void validateBasic() {
        if (objectIsAbsent(userModel)) {
            addError(UserModelError.UserNotExists());
        }
    }

    private void validateBasic(USERROLE expected) {
        if (objectIsAbsent(userModel)) {
            addError(UserModelError.UserNotExists(expected));
        }

    }

    public void validatePatient() {
        validateBasic(USERROLE.PATIENT);
        if (validationFailed()) return;

        UserRoleValidator userRoleValidator = userRoleValidator();
        userRoleValidator.validateIsPatient();
        validateUsingSubValidator(userRoleValidator);

    }

    public void validateUserOrPatient() {
        validateBasic();
        if (validationFailed()) return;

        UserRoleValidator userRoleValidator = userRoleValidator();
        userRoleValidator.validateIsUserOrPatient();
        validateUsingSubValidator(userRoleValidator);
    }
    public void validatePractitioner() {
        validateBasic(USERROLE.PRACTITIONER);
        if (validationFailed()) return;

        UserRoleValidator userRoleValidator = userRoleValidator();
        userRoleValidator.validateIsPractitioner();
        validateUsingSubValidator(userRoleValidator);
    }

    private UserRoleValidator userRoleValidator() {
        return new UserRoleValidator(userModel.getRole());
    }

    public static UserModelValidator ReferencedUserModelValidator(UserModel userModel) {
        return new UserModelValidator(userModel);
    }

}
