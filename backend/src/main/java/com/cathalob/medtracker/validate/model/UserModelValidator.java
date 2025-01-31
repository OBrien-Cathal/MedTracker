package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.exception.validation.UserModelValidationException;
import com.cathalob.medtracker.exception.validation.UserRoleValidationException;
import com.cathalob.medtracker.model.UserModel;

import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.validate.Validator;

import java.util.List;

public class UserModelValidator extends Validator {
    protected final UserModel userModel;
    protected final List<USERROLE> allowedRoles;

    public UserModelValidator(UserModel userModel, List<USERROLE> allowedRoles) {
        this.userModel = userModel;
        this.allowedRoles = allowedRoles;
    }

    protected void validateRole() {
        try {
            new UserRoleValidator(userModel.getRole(), allowedRoles).validate();
        } catch (UserRoleValidationException e) {
            addErrors(e.getErrors());
        }
    }


//    Static access

    public static PractitionerUserModelValidator PractitionerUserModelValidator(UserModel userModel) {
        return new PractitionerUserModelValidator(userModel);
    }

    public static PatientUserModelValidator PatientUserModelValidator(UserModel userModel) {
        return new PatientUserModelValidator(userModel);
    }

    public static PatientAndUserUserModelValidator PatientAndUserUserModelValidator(UserModel userModel) {
        return new PatientAndUserUserModelValidator(userModel);
    }


// Error Messages
    protected String objectNotPresentMessage() {
        return UserNotExists(allowedRoles);
    }


    public static String UserNotExists(List<USERROLE> expectedRole) {
        return String.join(",", expectedRole.stream().map(USERROLE::name).toList()) + " User does not exist";
    }

//    Overrides

    @Override
    protected void basicValidate() {
        validationObjectIsPresent(userModel);
        validateRole();
    }

    @Override
    protected void raiseValidationException() {
        throw new UserModelValidationException(getErrors());
    }
}
