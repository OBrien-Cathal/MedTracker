package com.cathalob.medtracker.mapper;

import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;

public class PatientRegistrationMapper {

    public static PatientRegistration PatientRegistration(
            UserModel userModel,
            UserModel practitionerUserModel) {
        return new PatientRegistration(null, userModel, practitionerUserModel, false);

    }
}
