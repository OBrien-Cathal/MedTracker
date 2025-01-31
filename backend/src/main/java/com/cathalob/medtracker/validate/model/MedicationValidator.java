package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.validate.Validator;

import java.util.List;

public class MedicationValidator extends Validator {


    public void validateMedication(Medication medication, List<Medication> existing){
         if (objectIsAbsent(medication)) {
             addError("Medication does not exist");
             return;
         };

         if(!existing.isEmpty()){
             addError("Medication already exists with name " + medication.getName());
         }
    }


    public static MedicationValidator aMedicationValidator(){
        return new MedicationValidator();
    }

    @Override
    protected void basicValidate() {

    }

    @Override
    protected void raiseValidationException() {

    }
}
