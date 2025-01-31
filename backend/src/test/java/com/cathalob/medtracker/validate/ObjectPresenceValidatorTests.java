package com.cathalob.medtracker.validate;

import com.cathalob.medtracker.exception.validation.ObjectPresenceValidatorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ObjectPresenceValidatorTests {
    @Test
    public void givenNothingToValidate_whenValidate_thenThrowException() {
        //given - precondition or setup
        String testValidatorName = "TEST Validator";
        // when - action or the behaviour that we are going test
        ObjectPresenceValidator objectPresenceValidator =
                ObjectPresenceValidator.aObjectPresenceValidator(null, testValidatorName);

        Assertions.assertThrows(ObjectPresenceValidatorException.class, objectPresenceValidator::validate);

        // then - verify the output
        assertThat(objectPresenceValidator.getErrors().size()).isEqualTo(1);
        assertThat(objectPresenceValidator.getErrors().get(0))
                .isEqualTo(ObjectPresenceValidator.ObjectMissingErrorMessage(testValidatorName));


    }
    @Test
    public void givenSomethingToValidate_whenValidate_thenValidateSuccessfully() {
        //given - precondition or setup
        String testValidatorName = "TEST Validator";
        // when - action or the behaviour that we are going test
        ObjectPresenceValidator objectPresenceValidator =
                ObjectPresenceValidator.aObjectPresenceValidator(testValidatorName, testValidatorName);

        objectPresenceValidator.validate();

        // then - verify the output
        assertThat(objectPresenceValidator.getErrors().isEmpty()).isTrue();



    }
}