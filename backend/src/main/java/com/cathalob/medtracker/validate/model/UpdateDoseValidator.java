package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.model.tracking.Dose;

public class UpdateDoseValidator extends DoseValidator {
    private Dose existingDose;

    public UpdateDoseValidator(Dose dose, Dose existingDose) {
        super(dose);
        this.existingDose = existingDose;
    }
}
