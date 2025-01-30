package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.testdata.UserModelBuilder;
import com.cathalob.medtracker.validate.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class UserRoleValidatorTests {
    @DisplayName("USER role is NOT valid patient")
    @Test
    public void givenUSERROLE_USER_whenALLOWED_PATIENT_thenReturnError() {
        //given - precondition or setup
        UserModel user = UserModelBuilder.aUserModel().build();
        // when - action or the behaviour that we are going test
        UserRoleValidator validator = new UserRoleValidator(user.getRole());
        validator.validateIsPatient();
        // then - verify the output
        assertThat(validator.validationFailed()).isTrue();
        assertThat(validator.getErrors()).hasSize(1);
        assertThat(validator.getErrors().get(0)).isEqualTo("User has role 'USER', where only 'PATIENT' are allowed.");
    }

    @DisplayName("ADMIN role is NOT valid patient or practitioner")
    @Test
    public void givenUSERROLE_ADMIN_whenALLOWED_PATIENT_PRACTITIONER_thenReturnError() {
        //given - precondition or setup
        UserModel user = UserModelBuilder.aUserModel().withRole(USERROLE.ADMIN).build();
        // when - action or the behaviour that we are going test
        UserRoleValidator validator = new UserRoleValidator(user.getRole());
        validator.validateIsPatientOrPractitioner();
        // then - verify the output
        assertThat(validator.validationFailed()).isTrue();
        assertThat(validator.getErrors()).hasSize(1);
        assertThat(validator.getErrors().get(0)).isEqualTo("User has role 'ADMIN', where only 'PATIENT, PRACTITIONER' are allowed.");
    }

    @DisplayName("PRACTITIONER role IS valid patient or practitioner")
    @Test
    public void givenUSERROLE_PRACTITIONER_whenALLOWED_PATIENT_PRACTITIONER_thenReturnError() {
        //given - precondition or setup
        UserModel user = UserModelBuilder.aUserModel().withRole(USERROLE.PRACTITIONER).build();
        // when - action or the behaviour that we are going test
        UserRoleValidator validator = new UserRoleValidator(user.getRole());
        validator.validateIsPatientOrPractitioner();
        // then - verify the output
        assertThat(validator.validationFailed()).isFalse();
    }

}