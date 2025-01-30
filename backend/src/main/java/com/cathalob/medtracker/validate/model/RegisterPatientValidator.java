package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.model.PatientRegistration;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.validate.Validator;

import java.util.List;

public class RegisterPatientValidator extends Validator {

    public void validateRegisterPatient(PatientRegistration patientRegistration, List<PatientRegistration> existing) {
        if (objectIsAbsent(patientRegistration.getUserModel())) {
            addError("No user to register");
        }

        UserModelValidator userModelValidator = UserModelValidator.ReferencedUserModelValidator(patientRegistration.getUserModel());
        userModelValidator.validateUserOrPatient();
        validateUsingSubValidator(userModelValidator);

        UserModelValidator practitionerValidator = UserModelValidator.ReferencedUserModelValidator(patientRegistration.getPractitionerUserModel());
        practitionerValidator.validatePractitioner();
        validateUsingSubValidator(practitionerValidator);
        PatientRegistration existingReg = existing.isEmpty() ? null : existing.get(0);
        if (existingReg != null) {
            addError("Registration already exists for patient and practitioner");
        }

    }


    public static RegisterPatientValidator aRegisterPatientValidator() {
        return new RegisterPatientValidator();
    }

}
