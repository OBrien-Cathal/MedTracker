package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.userroles.RoleChange;
import com.cathalob.medtracker.testdata.UserModelBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static com.cathalob.medtracker.testdata.RoleChangeBuilder.aRoleChange;
import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@ActiveProfiles("test")
class PatientRegistrationRepositoryTests {
    @Autowired
    private PatientRegistrationRepository patientRegistrationRepository;
    @Autowired
    private TestEntityManager testEntityManager;


    @Test
    public void givenPatientRegistration_whenSaved_thenReturnPatientRegistration() {
        //given - precondition or setup
        PatientRegistration patientRegistration = new PatientRegistration();
        RoleChange roleChange = aRoleChange().withNewRole(USERROLE.PATIENT).build();
        UserModel practitioner = UserModelBuilder.aUserModel().withRole(USERROLE.PRACTITIONER).build();

        patientRegistration.setPractitionerUserModel(practitioner);
        UserModel registeringUser = roleChange.getUserModel();
        patientRegistration.setUserModel(registeringUser);

        testEntityManager.persist(registeringUser);
        testEntityManager.persist(practitioner);
        testEntityManager.persist(roleChange);
        patientRegistration.setRoleChange(roleChange);

        // when - action or the behaviour that we are going test
        PatientRegistration saved = patientRegistrationRepository.save(patientRegistration);
        // then - verify the output
        assertThat(saved.getId()).isGreaterThan(0L);
    }
}