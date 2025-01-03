package com.cathalob.medtracker.service.api.impl;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.userroles.RoleChange;
import com.cathalob.medtracker.payload.response.PatientRegistrationResponse;
import com.cathalob.medtracker.repository.PatientRegistrationRepository;
import com.cathalob.medtracker.repository.RoleChangeRepository;
import com.cathalob.medtracker.service.UserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static com.cathalob.medtracker.testdata.RoleChangeBuilder.aRoleChange;
import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
class PatientsServiceTests {
    @Mock RoleChangeRepository roleChangeRepository;
    @Mock
    private PatientRegistrationRepository patientRegistrationRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private PatientsServiceApi patientsService;
    @Disabled("Move to integration tests")
    @DisplayName("Get Patients returns ")
    @Test
    public void givenExistingPatient_whenGetPatientAsPractitioner_thenReturnPatientUserModels() {
        //given - precondition or setup
//        missing role, test still passes and should not
        List<UserModel> patients = List.of(
                aUserModel().withRole(USERROLE.PATIENT).withId(1L).build(),
                aUserModel().withRole(USERROLE.PATIENT).withId(2L).build());
        UserModel practitioner = aUserModel().withId(2L).withRole(USERROLE.PRACTITIONER).build();
        UserModel practitioner2 = aUserModel().withId(2L).withRole(USERROLE.PRACTITIONER).build();
        List<PatientRegistration> patientRegistrations = List.of(
                new PatientRegistration(1L, patients.get(0), practitioner, new RoleChange()),
                new PatientRegistration(2L, patients.get(1), practitioner2, new RoleChange()));
        given(userService.findByLogin(practitioner.getUsername())).willReturn(practitioner);
        given(patientRegistrationRepository.findByPractitionerUserModel(practitioner)).willReturn(patientRegistrations.subList(0,2));
        given(userService.findUserModelsById(patientRegistrations
                .stream().
                map((patientRegistration -> patientRegistration
                        .getUserModel().getId())).toList()))
                .willReturn(patients);
        // when - action or the behaviour that we are going test
        List<UserModel> patientUserModels = patientsService.getPatientUserModels(practitioner.getUsername());
        // then - verify the output
        assertThat(patientUserModels).size().isEqualTo(2);
    }



    @Test
    public void givenPatientRegistrationRequest_whenRegisterPatient_thenReturnPatientRegistrationResponse() {
        //given - precondition or setup
        String usernameToRegister = "user@user.com";
        RoleChange roleChange = aRoleChange().withNewRole(USERROLE.PATIENT).build();
        UserModel practitioner = aUserModel().withId(1L).withRole(USERROLE.PRACTITIONER).build();
        given(userService.findByLogin(usernameToRegister)).willReturn(roleChange.getUserModel());
        PatientRegistration patientRegistration = new PatientRegistration(
                1L,
                roleChange.getUserModel(),
                practitioner,
                roleChange);
        given(userService.findUserModelById(1L)).willReturn(Optional.of(practitioner));
        given(roleChangeRepository.save(any(RoleChange.class))).willReturn(roleChange);
        given(patientRegistrationRepository.save(any(PatientRegistration.class))).willReturn(patientRegistration);

        // when - action or the behaviour that we are going test
        PatientRegistrationResponse patientRegistrationResponse = patientsService.registerPatient(usernameToRegister, practitioner.getId());

        assertThat(patientRegistrationResponse.getData().getPractitionerId()).isEqualTo(practitioner.getId());
        assertThat(patientRegistrationResponse.getData()).isNotNull();
        assertThat(patientRegistrationResponse.getData().getId()).isEqualTo(1L);
        assertThat(patientRegistrationResponse.getMessage()).isEqualTo("Registration Pending");
        assertThat(patientRegistrationResponse.getErrors()).isNull();

        // then - verify the output
    }


}