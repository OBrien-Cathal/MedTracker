package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.exception.validation.DailyEvaluationValidationException;
import com.cathalob.medtracker.exception.validation.dose.DailyDoseDataException;
import com.cathalob.medtracker.model.tracking.Dose;
import com.cathalob.medtracker.validate.Validator;

import java.time.LocalDateTime;

public class DoseValidator extends Validator {

    private final Dose dose;

    public DoseValidator(Dose dose) {
        this.dose = dose;
    }

    @Override
    protected void basicValidate() {
        validateDailyEvaluation();
        validatePrescriptionScheduleEntry();
        if (validationFailed()) return;

        validateDoseReadingTime();
    }

    @Override
    protected void raiseValidationException() {
        throw new DailyDoseDataException(getErrors());
    }

    private void validateDoseReadingTime() {
        LocalDateTime doseTime = dose.getDoseTime();
        if (doseTime.toLocalDate().isBefore(LocalDateTime.now().toLocalDate())) {
            addError("Cannot submit dose readings before the schedule day");
        }

        LocalDateTime prescriptionBegin = dose.getPrescriptionScheduleEntry().getPrescription().getBeginTime();
        if (doseTime.isBefore(prescriptionBegin)) {
            addError(String.format("Cannot enter dose data for %s before prescription begin time %s", doseTime, prescriptionBegin ));
        }

    }
    private void validateDailyEvaluation() {
        try {
            DailyEvaluationValidator.aDailyEvaluationValidator(dose.getEvaluation()).validate();
        } catch (DailyEvaluationValidationException e) {
            addErrors(e.getErrors());
        }
    }

    private void validatePrescriptionScheduleEntry() {

        PrescriptionScheduleEntryValidator.aPrescriptionScheduleEntryValidator(dose.getPrescriptionScheduleEntry()).validate();

    }

    public static DoseValidator AddDoseValidator(Dose dose) {
        return new AddDoseValidator(dose);
    }

    public static DoseValidator UpdateDoseValidator(Dose dose, Dose existingDose) {
        return new UpdateDoseValidator(dose, existingDose);
    }
}
