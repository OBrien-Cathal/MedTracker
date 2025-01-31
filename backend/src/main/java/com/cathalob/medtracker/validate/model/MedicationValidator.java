package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.exception.validation.medication.MedicationValidationException;
import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.validate.Validator;

public class MedicationValidator extends Validator {

    private Medication medication;
    private Medication existingMedication;

    public MedicationValidator(Medication medication, Medication existingMedication) {
        this.medication = medication;
        this.existingMedication = existingMedication;
    }

    @Override
    protected void basicValidate() {
        validateObjectPresence(medication);

        if (existingMedication != null) {
            addError("Medication already exists with name " + medication.getName());
        }
    }

    @Override
    protected void raiseValidationException() {
        throw new MedicationValidationException(getErrors());
    }

    public static MedicationValidator AMedicationValidator(Medication medication, Medication existingMedication) {
        return new MedicationValidator(medication, existingMedication);
    }
}
