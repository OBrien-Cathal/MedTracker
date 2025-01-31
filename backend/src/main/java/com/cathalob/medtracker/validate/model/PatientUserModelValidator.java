package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;

import java.util.List;

public class PatientUserModelValidator extends UserModelValidator{
    public PatientUserModelValidator(UserModel userModel) {
        super(userModel);
    }

    protected void validateRole() {
        new UserRoleValidator(userModel.getRole(), List.of(USERROLE.PATIENT));
    }
}
