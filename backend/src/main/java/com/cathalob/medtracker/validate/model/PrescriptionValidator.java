package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.validate.Validator;

public class PrescriptionValidator extends Validator {

    public PrescriptionValidator() {
    }

    public void validatePrescription(Prescription prescription) {
        if (!validateExists(prescription)) {
            addError("Prescription does not exist");
        }
    }

    public static PrescriptionValidator aPrescriptionValidator() {
        return new PrescriptionValidator();
    }

}
