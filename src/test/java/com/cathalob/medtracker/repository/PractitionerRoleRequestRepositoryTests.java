package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.PractitionerRoleRequest;
import com.cathalob.medtracker.model.UserModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@ActiveProfiles("test")

class PractitionerRoleRequestRepositoryTests {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    PractitionerRoleRequestRepository practitionerRoleRequestRepository;

    @Test
    public void givenPractitionerRoleRequest_whenSave_thenReturnSavedPractitionerRoleRequest() {
        //given - precondition or setup
        PractitionerRoleRequest practitionerRoleRequest = new PractitionerRoleRequest();

        UserModel userModelWithRoleUSER = aUserModel().build();
        testEntityManager.persist(userModelWithRoleUSER);
        practitionerRoleRequest.setUserModel(userModelWithRoleUSER);

        // when - action or the behaviour that we are going test
        PractitionerRoleRequest saved = practitionerRoleRequestRepository.save(practitionerRoleRequest);

        // then - verify the output
        assertThat(saved.getId()).isGreaterThan(1L);

    }
}