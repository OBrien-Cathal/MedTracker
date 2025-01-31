package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.exception.validation.PatientRegistrationException;
import com.cathalob.medtracker.exception.validation.UserModelValidationException;
import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.validate.Validator;

public class PatientRegistrationValidator extends Validator {
    private final PatientRegistration patientRegistration;
    private final PatientRegistration existingPatientRegistration;

    public PatientRegistrationValidator(PatientRegistration patientRegistration, PatientRegistration existingPatientRegistration) {
        this.patientRegistration = patientRegistration;
        this.existingPatientRegistration = existingPatientRegistration;
    }

    public static PatientRegistrationValidator aRegisterPatientValidator(
            PatientRegistration patientRegistration, PatientRegistration existingPatientRegistration) {
        return new PatientRegistrationValidator(patientRegistration, existingPatientRegistration);
    }

    @Override
    protected void basicValidate() {
        validateObjectPresence(patientRegistration.getUserModel());
        try {
            UserModelValidator.PatientAndUserUserModelValidator(patientRegistration.getUserModel()).validate();

            UserModelValidator.PractitionerUserModelValidator(patientRegistration.getPractitionerUserModel()).validate();
        } catch (UserModelValidationException e) {
            addErrors(e.getErrors());
        }

        if (existingPatientRegistration != null) {
            addError("Registration already exists for patient and practitioner");
        }
    }

    @Override
    protected void raiseValidationException() {
        throw new PatientRegistrationException(getErrors());
    }
}
