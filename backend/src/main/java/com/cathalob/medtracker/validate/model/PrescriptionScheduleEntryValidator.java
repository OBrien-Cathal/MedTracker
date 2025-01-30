package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import com.cathalob.medtracker.validate.Validator;


public class PrescriptionScheduleEntryValidator extends Validator {

    public PrescriptionScheduleEntryValidator() {
    }

    public void validatePrescriptionScheduleEntry(PrescriptionScheduleEntry prescriptionScheduleEntry) {
        if (objectIsAbsent(prescriptionScheduleEntry)) {
            addError("PrescriptionScheduleEntry does not exist");
            return;
        }
        validatePrescription(prescriptionScheduleEntry.getPrescription());
    }

    public void validatePrescription(Prescription prescription) {
        PrescriptionValidator prescriptionValidator = PrescriptionValidator.aPrescriptionValidator();
        prescriptionValidator.validatePrescription(prescription);
        validateUsingSubValidator(prescriptionValidator);

    }

    public static PrescriptionScheduleEntryValidator aPrescriptionScheduleEntryValidator() {
        return new PrescriptionScheduleEntryValidator();
    }

}
