package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.exception.validation.UserRoleValidationException;
import com.cathalob.medtracker.model.enums.USERROLE;

import com.cathalob.medtracker.validate.Validator;

import java.util.List;

public class UserRoleValidator extends Validator {
    private final USERROLE userRole;

    public UserRoleValidator(USERROLE userRole) {
        super();
        this.userRole = userRole;
    }

    public void validateIsPatient() {
        validateRoleInAllowed(List.of(USERROLE.PATIENT));

    }

    public void validateIsPatientOrPractitioner() {
        validateRoleInAllowed(List.of(USERROLE.PATIENT, USERROLE.PRACTITIONER));
    }

    private void validateRoleInAllowed(List<USERROLE> allowedRoles) {
        if (!allowedRoles.contains(userRole)) {
            this.addError(UserRoleValidator.wrongRoleErrorMessage(userRole, allowedRoles));
        }
    }

    public void is(List<USERROLE> allowedRoles){
        if (!allowedRoles.contains(userRole)) {
            this.addError(UserRoleValidator.wrongRoleErrorMessage(userRole, allowedRoles));
            throw new UserRoleValidationException(this.getErrors());
        }
    }

    public static String wrongRoleErrorMessage(USERROLE current, List<USERROLE> allowed) {
        return String.format("User has role '%s', where only '%s' are allowed.", current,
                String.join(", ", allowed.stream().map(USERROLE::name).toList()));
    }
}
