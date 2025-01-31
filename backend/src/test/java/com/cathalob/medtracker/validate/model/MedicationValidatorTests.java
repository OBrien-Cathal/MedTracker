package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.exception.validation.medication.MedicationValidationException;
import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.testdata.MedicationBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class MedicationValidatorTests {

    @Test
    public void givenExistingMedicationName_whenAddMedication_thenRaiseError() {
        //given - precondition or setup
        Medication med = MedicationBuilder.aMedication().withName("Med").build();
        // when - action or the behaviour that we are going test
        MedicationValidator validator = MedicationValidator.AMedicationValidator(med, med);
        Assertions.assertThrows(MedicationValidationException.class, validator::validate);
        // then - verify the output
        assertThat(validator.getErrors().isEmpty()).isFalse();

    }

    @Test
    public void givenNewMedication_whenAddMedication_thenSucceed() {
        //given - precondition or setup
        Medication med = MedicationBuilder.aMedication().withName("Med").build();
        // when - action or the behaviour that we are going test
        MedicationValidator validator = MedicationValidator.AMedicationValidator(med, null);
        // then - verify the output
        assertThat(validator.getErrors().isEmpty()).isTrue();

    }

}