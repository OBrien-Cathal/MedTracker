package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.exception.validation.PrescriptionScheduleEntryValidatorException;
import com.cathalob.medtracker.exception.validation.PrescriptionValidatorException;
import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import com.cathalob.medtracker.validate.Validator;


public class PrescriptionScheduleEntryValidator extends Validator {

    private final PrescriptionScheduleEntry prescriptionScheduleEntry;
    private final PrescriptionScheduleEntry existingPrescriptionScheduleEntry;

    public PrescriptionScheduleEntryValidator(PrescriptionScheduleEntry prescriptionScheduleEntry, PrescriptionScheduleEntry existingPrescriptionScheduleEntry) {
        this.prescriptionScheduleEntry = prescriptionScheduleEntry;
        this.existingPrescriptionScheduleEntry = existingPrescriptionScheduleEntry;
    }

    @Override
    protected void basicValidate() {
        validatePrescriptionScheduleEntry();
    }

    @Override
    protected void raiseValidationException() {
        throw new PrescriptionScheduleEntryValidatorException(getErrors());
    }

    public void validatePrescriptionScheduleEntry() {
        validateObjectPresence(prescriptionScheduleEntry);
        validatePrescription(prescriptionScheduleEntry.getPrescription());
    }

    public void validatePrescription(Prescription prescription) {
        try {
            PrescriptionValidator.aPrescriptionValidator(prescription).validate();
        } catch (PrescriptionValidatorException e) {
            addErrors(e.getErrors());
        }

    }

    public static PrescriptionScheduleEntryValidator aPrescriptionScheduleEntryValidator(PrescriptionScheduleEntry prescriptionScheduleEntry) {
        return new PrescriptionScheduleEntryValidator(prescriptionScheduleEntry, null);
    }

}
