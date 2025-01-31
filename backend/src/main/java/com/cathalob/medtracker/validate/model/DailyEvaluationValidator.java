package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.exception.validation.DailyEvaluationValidationException;
import com.cathalob.medtracker.exception.validation.UserModelValidationException;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.validate.Validator;

public class DailyEvaluationValidator extends Validator {
    private final DailyEvaluation dailyEvaluation;

    public DailyEvaluationValidator(DailyEvaluation dailyEvaluation) {
        this.dailyEvaluation = dailyEvaluation;
    }

    @Override
    protected void basicValidate() {
        validatePatient(dailyEvaluation.getUserModel());
    }

    @Override
    protected void raiseValidationException() {
        throw new DailyEvaluationValidationException(getErrors());
    }

    public void validatePatient(UserModel patient) {
        try {
            UserModelValidator.PatientUserModelValidator(patient).validate();
        } catch (UserModelValidationException e) {
            addErrors(e.getErrors());
        }
    }

    public static DailyEvaluationValidator aDailyEvaluationValidator(DailyEvaluation dailyEvaluation) {
        return new DailyEvaluationValidator(dailyEvaluation);
    }

}
