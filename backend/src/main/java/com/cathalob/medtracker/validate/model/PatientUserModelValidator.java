package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;

import java.util.List;

public class PatientUserModelValidator extends UserModelValidator{
    public PatientUserModelValidator(UserModel userModel) {
        super(userModel, allowedRoles() );
    }


    protected static List<USERROLE> allowedRoles(){
        return List.of(USERROLE.PATIENT);
    }



}
