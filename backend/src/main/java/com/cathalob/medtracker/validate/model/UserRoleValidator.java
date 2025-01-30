package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.model.enums.USERROLE;

import com.cathalob.medtracker.validate.Validator;
import com.cathalob.medtracker.validate.model.errors.UserRoleError;

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

    public void validateIsUserOrPatient() {
        validateRoleInAllowed(List.of(USERROLE.USER, USERROLE.PATIENT));
    }

    public Validator validateIsAdmin() {
        return validateRoleInAllowed(List.of(USERROLE.ADMIN));
    }

    public void validateIsPractitioner() {
        validateRoleInAllowed(List.of(USERROLE.PRACTITIONER));
    }

    public void validateIsPatientOrPractitioner() {
        validateRoleInAllowed(List.of(USERROLE.PATIENT, USERROLE.PRACTITIONER));
    }

    private Validator validateRoleInAllowed(List<USERROLE> allowedRoles) {
        if (!allowedRoles.contains(userRole)) {
            this.addError(UserRoleError.of(userRole, allowedRoles));
            System.out.println(this.getErrors().stream().findAny());
        }
        return this;
    }


}
