package com.cathalob.medtracker.exception.validation;

import com.cathalob.medtracker.model.enums.USERROLE;

import java.util.List;

public class UserRoleValidationException extends ValidatorException{

    public UserRoleValidationException(List<String> errors) {
        super(errors);
    }
}
