package com.cathalob.medtracker.repository;


import com.cathalob.medtracker.model.UserModel;

import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.assertj.core.api.Assertions.assertThat;

import com.cathalob.medtracker.model.enums.USERROLE;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;


@DataJpaTest
@ActiveProfiles("test")
class UserModelRepositoryTests {

    @Autowired
    private UserModelRepository userModelRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeAll
    public static void setUp(){

    }
    @Test
    public void givenUserModel_whenSaved_thenReturnSavedUserModel() {
//        given
        UserModel userModel = aUserModel().build();

//        when
        UserModel saved = userModelRepository.save(userModel);

//        then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isGreaterThan(1L);
        assertThat(saved.getPassword()).isEqualTo("abc");
    }

    @Test
    public void givenUSERROLE_USER_whenFindRole_thenReturnExistingUserModelsWithRoleUSER() {
        //given - precondition or setup
        persistUserForAllRoles();

        // when - action or the behaviour that we are going test
        List<UserModel> byRole = userModelRepository.findByRole(USERROLE.USER);

        // then - verify the output
        assertThat(byRole.size()).isEqualTo(1);
    }

    @Test
    public void givenUSERROLE_PRACT_whenFindRole_thenReturnExistingUserModelsWithRolePRACT() {
        //given - precondition or setup
        persistUserForAllRoles();

        // when - action or the behaviour that we are going test
        List<UserModel> byRole = userModelRepository.findByRole(USERROLE.PRACTITIONER);

        // then - verify the output
        assertThat(byRole.size()).isEqualTo(1);
    }

    @Test
    public void givenUSERROLE_ADMIN_whenFindRole_thenReturnExistingUserModelsWithRoleADMIN() {
        //given - precondition or setup
        persistUserForAllRoles();

        // when - action or the behaviour that we are going test
        List<UserModel> byRole = userModelRepository.findByRole(USERROLE.USER);

        // then - verify the output
        assertThat(byRole.size()).isEqualTo(1);
    }


    private void persistUserForAllRoles() {
        UserModel userModelWithRoleUSER = aUserModel().build();
        testEntityManager.persist(userModelWithRoleUSER);
        testEntityManager.persist(aUserModel().withRole(USERROLE.PRACTITIONER).build());
        testEntityManager.persist(aUserModel().withRole(USERROLE.ADMIN).build());
    }

}