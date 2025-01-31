package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.exception.validation.PrescriptionValidatorException;
import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.validate.Validator;

public class PrescriptionValidator extends Validator {

    private final Prescription prescription;
    private final Prescription existingPrescription;

    public PrescriptionValidator(Prescription prescription, Prescription existingPrescription) {
        this.prescription = prescription;
        this.existingPrescription = existingPrescription;
    }

    @Override
    protected void basicValidate() {
        validatePrescription();
    }

    @Override
    protected void raiseValidationException() {
        throw new PrescriptionValidatorException(getErrors());
    }

    public void validatePrescription() {
        if (objectIsAbsent(prescription)) {
            addError("Prescription does not exist");
        }
    }

    public static PrescriptionValidator aPrescriptionValidator(Prescription prescription) {
        return new PrescriptionValidator(prescription, null);
    }

}
