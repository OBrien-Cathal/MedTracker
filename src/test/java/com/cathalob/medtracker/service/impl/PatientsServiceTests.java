package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.payload.data.PatientRegistrationData;
import com.cathalob.medtracker.payload.response.PatientRegistrationResponse;
import com.cathalob.medtracker.repository.PatientRegistrationRepository;
import com.cathalob.medtracker.service.UserService;
import com.cathalob.medtracker.service.impl.PatientsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
class PatientsServiceTests {

    @Mock
    private PatientRegistrationRepository patientRegistrationRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private PatientsService patientsService;


    @DisplayName("Get patient registrations returns items based on Role of requester ")
    @Test
    public void givenExistingPatientRegistrations_whenGetPatientRegistrationsAsPractitioner_thenReturnPatientRegistrations() {
        //given - precondition or setup

        List<UserModel> patients = List.of(
                aUserModel().withRole(USERROLE.PATIENT).withId(1L).build(),
                aUserModel().withRole(USERROLE.PATIENT).withId(2L).build());
        UserModel practitioner = aUserModel().withId(2L).withRole(USERROLE.PRACTITIONER).build();

        List<PatientRegistration> patientRegistrations = List.of(
                new PatientRegistration(1L, patients.get(0), practitioner, false),
                new PatientRegistration(2L, patients.get(1), practitioner, false));
        given(userService.findByLogin(practitioner.getUsername())).willReturn(practitioner);
        given(patientRegistrationRepository.findByPractitionerUserModel(practitioner)).willReturn(patientRegistrations.subList(0, 2));
        // when - action or the behaviour that we are going test
        List<PatientRegistrationData> registrations = patientsService.getPatientRegistrations(practitioner.getUsername());
        // then - verify the output
        assertThat(registrations).size().isEqualTo(2);
    }

    @DisplayName("Successful patient registration returns success response")
    @Test
    public void givenPatientRegistrationRequest_whenRegisterPatient_thenReturnPatientRegistrationResponse() {
        //given - precondition or setup

        UserModel toRegister = aUserModel().build();
        UserModel practitioner = aUserModel().withId(1L).withRole(USERROLE.PRACTITIONER).build();
        given(userService.findByLogin(toRegister.getUsername())).willReturn(toRegister);
        PatientRegistration patientRegistration = new PatientRegistration(
                1L,
                toRegister,
                practitioner,
                false);
        given(userService.findUserModelById(1L)).willReturn(Optional.of(practitioner));
        given(patientRegistrationRepository.save(any(PatientRegistration.class))).willReturn(patientRegistration);

        // when - action or the behaviour that we are going test
        PatientRegistrationResponse patientRegistrationResponse = patientsService.registerPatient(toRegister.getUsername(), practitioner.getId());

        // then - verify the output
        assertThat(patientRegistrationResponse.getData().getPractitionerId()).isEqualTo(practitioner.getId());
        assertThat(patientRegistrationResponse.getData()).isNotNull();
        assertThat(patientRegistrationResponse.getData().getId()).isEqualTo(1L);
        assertThat(patientRegistrationResponse.isSuccessful()).isTrue();
        assertThat(patientRegistrationResponse.getMessage()).isEqualTo("Succeeded");
        assertThat(patientRegistrationResponse.getErrors()).isEmpty();
    }

    @DisplayName("Fail validation: (registerPatient) due to non existent practitioner user")
    @Test
    public void givenPatientRegistrationRequestWithBogusPractitionerId_whenRegisterPatient_thenReturnFailedPatientRegistrationResponse() {
        //given - precondition or setup
        UserModel toRegister = aUserModel().build();
        UserModel practitioner = aUserModel().withId(1L).withRole(USERROLE.PRACTITIONER).build();
        given(userService.findByLogin(toRegister.getUsername())).willReturn(toRegister);
        given(userService.findUserModelById(practitioner.getId())).willReturn(Optional.empty());

        // when - action or the behaviour that we are going test
        PatientRegistrationResponse patientRegistrationResponse = patientsService.registerPatient(toRegister.getUsername(), practitioner.getId());

        // then - verify the output
        assertThat(patientRegistrationResponse.isSuccessful()).isFalse();
        assertThat(patientRegistrationResponse.getMessage()).isEqualTo("Failed Validation");
        assertThat(patientRegistrationResponse.getErrors()).isNotEmpty();
    }


    @DisplayName("Fail validation: (registerPatient) - already existing registration for combination")
    @Test
    public void givenPatientRegistrationRequestAndExistingPatientRegistration_whenRegisterPatient_thenReturnFailedPatientRegistrationResponse() {
        //given - precondition or setup
        UserModel toRegister = aUserModel().build();
        UserModel practitioner = aUserModel().withId(1L).withRole(USERROLE.PRACTITIONER).build();
        given(userService.findByLogin(toRegister.getUsername())).willReturn(toRegister);
        given(userService.findUserModelById(practitioner.getId())).willReturn(Optional.of(practitioner));
        PatientRegistration patientRegistration = new PatientRegistration(1L, toRegister, practitioner, false);
        given(patientRegistrationRepository.findByUserModelAndPractitionerUserModel(toRegister, practitioner)).willReturn(List.of(patientRegistration));

        // when - action or the behaviour that we are going test
        PatientRegistrationResponse patientRegistrationResponse = patientsService.registerPatient(toRegister.getUsername(), practitioner.getId());

        // then - verify the output
        assertThat(patientRegistrationResponse.isSuccessful()).isFalse();
        assertThat(patientRegistrationResponse.getMessage()).isEqualTo("Failed Validation");
        assertThat(patientRegistrationResponse.getErrors()).isNotEmpty();
    }

    @DisplayName("Fail validation: (registerPatient) - user to register has wrong role")
    @Test
    public void givenPatientRegistrationRequestForADMIN_whenRegisterPatient_thenReturnFailedPatientRegistrationResponse() {
        //given - precondition or setup
        UserModel toRegister = aUserModel().withRole(USERROLE.ADMIN).build();
        UserModel practitioner = aUserModel().withId(1L).withRole(USERROLE.PRACTITIONER).build();
        given(userService.findByLogin(toRegister.getUsername())).willReturn(toRegister);
        given(userService.findUserModelById(practitioner.getId())).willReturn(Optional.of(practitioner));
        PatientRegistration patientRegistration = new PatientRegistration(1L, toRegister, practitioner, false);
//        given(patientRegistrationRepository.findByUserModelAndPractitionerUserModel(toRegister, practitioner)).willReturn(List.of(patientRegistration));

        // when - action or the behaviour that we are going test
        PatientRegistrationResponse patientRegistrationResponse = patientsService.registerPatient(toRegister.getUsername(), practitioner.getId());

        // then - verify the output
        assertThat(patientRegistrationResponse.isSuccessful()).isFalse();
        assertThat(patientRegistrationResponse.getMessage()).isEqualTo("Failed Validation");
        assertThat(patientRegistrationResponse.getErrors()).isNotEmpty();
    }


    @DisplayName("Fail validation: (approvePatientRegistration) - user to register has wrong role")
    @Test
    public void givenApprovePatientRegistrationRequest_whenRegisterPatient_thenReturnFailedPatientRegistrationResponse() {
        //given - precondition or setup
        UserModel toRegister = aUserModel().withRole(USERROLE.ADMIN).build();
        UserModel practitioner = aUserModel().withId(1L).withRole(USERROLE.PRACTITIONER).build();
        given(userService.findByLogin(toRegister.getUsername())).willReturn(toRegister);
        given(userService.findUserModelById(practitioner.getId())).willReturn(Optional.of(practitioner));
        PatientRegistration patientRegistration = new PatientRegistration(1L, toRegister, practitioner, false);
//        given(patientRegistrationRepository.findByUserModelAndPractitionerUserModel(toRegister, practitioner)).willReturn(List.of(patientRegistration));

        // when - action or the behaviour that we are going test
        PatientRegistrationResponse patientRegistrationResponse = patientsService.registerPatient(toRegister.getUsername(), practitioner.getId());

        // then - verify the output
        assertThat(patientRegistrationResponse.isSuccessful()).isFalse();
        assertThat(patientRegistrationResponse.getMessage()).isEqualTo("Failed Validation");
        assertThat(patientRegistrationResponse.getErrors()).isNotEmpty();
    }


}