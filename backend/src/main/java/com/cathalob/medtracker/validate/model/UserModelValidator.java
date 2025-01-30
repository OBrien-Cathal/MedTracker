package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.model.UserModel;

import com.cathalob.medtracker.validate.Validator;
import com.cathalob.medtracker.validate.model.errors.UserModelError;

public class UserModelValidator extends Validator {
    private final UserModel userModel;

    public UserModelValidator(UserModel userModel) {
        this.userModel = userModel;
    }


    public void validatePatient() {
        if (objectIsAbsent(userModel)) {
            addError(UserModelError.UserNotExists());
            return;
        }
        UserRoleValidator userRoleValidator = new UserRoleValidator(userModel.getRole());
        userRoleValidator.validateIsPatient();
        validateUsingSubValidator(userRoleValidator);

    }


    public static UserModelValidator ReferencedUserModelValidator(UserModel userModel) {
        return new UserModelValidator(userModel);
    }

}
