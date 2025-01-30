package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.model.tracking.Dose;
import com.cathalob.medtracker.validate.Validator;

import java.time.LocalDateTime;

public class DoseValidator extends Validator {

    public DoseValidator() {
    }

    public void validateDoseEntry(Dose dose) {
        if (dose.getId() == null) {
            validateAddDose(dose);
        } else {
            validateUpdateDose(dose);
        }
    }

    private void validateAddDose(Dose dose) {
        validateDailyEvaluation(dose.getEvaluation());
        validatePrescriptionScheduleEntry(dose.getPrescriptionScheduleEntry());
    }

    private void validateUpdateDose(Dose dose) {
        validateDailyEvaluation(dose.getEvaluation());
        validatePrescriptionScheduleEntry(dose.getPrescriptionScheduleEntry());
        if (validationFailed()) return;

        validateDoseReadingTime(dose, dose.getPrescriptionScheduleEntry().getPrescription());
    }

    private void validateDoseReadingTime(Dose dose, Prescription prescription) {
        if (dose.getDoseTime().toLocalDate().isBefore(LocalDateTime.now().toLocalDate())) {
            addError("Cannot submit dose readings before the schedule day");
        }

        if (dose.getDoseTime().isBefore(prescription.getBeginTime())) {
            addError("Cannot enter dose data before prescription begin time");
        }

    }

    private void validateDailyEvaluation(DailyEvaluation dailyEvaluation) {
        new DailyEvaluationValidator().validate(dailyEvaluation);
    }

    private void validatePrescriptionScheduleEntry(PrescriptionScheduleEntry prescriptionScheduleEntry) {
        PrescriptionScheduleEntryValidator prescriptionScheduleEntryValidator = PrescriptionScheduleEntryValidator.aPrescriptionScheduleEntryValidator();

        prescriptionScheduleEntryValidator
                .validatePrescriptionScheduleEntry(prescriptionScheduleEntry);
        validateUsingSubValidator(prescriptionScheduleEntryValidator);


    }

    public static DoseValidator aDoseValidator() {
        return new DoseValidator();
    }
}
