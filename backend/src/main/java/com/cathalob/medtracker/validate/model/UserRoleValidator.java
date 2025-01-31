package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.exception.validation.UserRoleValidationException;
import com.cathalob.medtracker.model.enums.USERROLE;

import com.cathalob.medtracker.validate.Validator;

import java.util.List;

public class UserRoleValidator extends Validator {
    private final USERROLE userRole;
    private final List<USERROLE> allowedRoles;

    public UserRoleValidator(USERROLE userRole, List<USERROLE> allowedRoles) {
        super();
        this.userRole = userRole;
        this.allowedRoles = allowedRoles;
    }

    public void validateAllowedRoles() {
        if (!allowedRoles.contains(userRole)) {
            this.addError(UserRoleValidator.wrongRoleErrorMessage(userRole, allowedRoles));
        }
    }

    @Override
    protected void basicValidate() {
        validateAllowedRoles();
    }

    @Override
    protected void raiseValidationException() {
        throw new UserRoleValidationException(this.getErrors());
    }

    public static String wrongRoleErrorMessage(USERROLE current, List<USERROLE> allowed) {
        return String.format("User has role '%s', where only '%s' are allowed.", current,
                String.join(", ", allowed.stream().map(USERROLE::name).toList()));
    }

    public static UserRoleValidator PatientRole(USERROLE role){
        return new UserRoleValidator(role, List.of(USERROLE.PATIENT));
    }




}
