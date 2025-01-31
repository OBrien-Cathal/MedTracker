package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.exception.validation.UserRoleValidationException;
import com.cathalob.medtracker.model.UserModel;

import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.validate.Validator;

import java.util.List;

public class UserModelValidator extends Validator {
    protected final UserModel userModel;

    public UserModelValidator(UserModel userModel) {
        this.userModel = userModel;
    }

    private void validateBasic(List<USERROLE> userRoles) {
        if (objectIsAbsent(userModel)) {
            addError(UserModelValidator.UserNotExists(userRoles));
        }
    }

    protected void validateRole() {
    }

    public void validatePatient() {
        validateRole(List.of(USERROLE.PATIENT));
    }

    public void validateUserOrPatient() {
        validateRole(List.of(USERROLE.PATIENT, USERROLE.USER));
    }

    public void validatePractitioner() {
        validateRole(List.of(USERROLE.PRACTITIONER));
    }


    private void validateRole(List<USERROLE> userRoles) {
        validateBasic(userRoles);
        if (validationFailed()) return;
        try {
            userRoleValidator(userRoles).validate();
        } catch (UserRoleValidationException e) {
            addErrors(e.getErrors());
        }
    }

    private UserRoleValidator userRoleValidator(List<USERROLE> allowedRoles) {
        return new UserRoleValidator(userModel.getRole(), allowedRoles);
    }


//    Static access

    public static UserModelValidator ReferencedUserModelValidator(UserModel userModel) {
        return new UserModelValidator(userModel);
    }

    public static PractitionerUserModelValidator PractitionerUserModelValidator(UserModel userModel) {
        return new PractitionerUserModelValidator(userModel);
    }

    public static PatientUserModelValidator PatientUserModelValidator(UserModel userModel) {
        return new PatientUserModelValidator(userModel);
    }


// Error Messages

    public static String UserNotExists() {
        return "User does not exist";
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
        throw new UserRoleValidationException(getErrors());
    }
}
