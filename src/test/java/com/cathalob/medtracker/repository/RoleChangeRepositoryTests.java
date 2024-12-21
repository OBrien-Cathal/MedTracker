package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.userroles.RoleChange;
import com.cathalob.medtracker.testdata.UserModelBuilder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static com.cathalob.medtracker.testdata.RoleChangeBuilder.aRoleChange;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql(scripts = "classpath:clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class RoleChangeRepositoryTests {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private RoleChangeRepository roleChangeRepository;

    @Test
    @Order(1)
    public void givenRoleChange_whenSave_thenReturnSavedRoleChange() {
        //given - precondition or setup
        System.out.println("first");
        RoleChange roleChange = aRoleChange().build();
        testEntityManager.persist(roleChange.getUserModel());
        // when - action or the behaviour that we are going test
        RoleChange saved = roleChangeRepository.save(roleChange);
        // then - verify the output
        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getApprovedBy()).isNull();
        assertThat(saved.getApprovalTime()).isNull();
    }

    @Test
    @Order(2)
    public void givenApprovedRoleChange_whenSave_thenReturnSavedRoleChange() {
        //given - precondition or setup
        System.out.println("second");
        UserModelBuilder approvedBy = UserModelBuilder.aUserModel().withRole(USERROLE.ADMIN);
        RoleChange roleChange = aRoleChange().withApprovedByUserModelBuilder(approvedBy).withApprovalTime(LocalDateTime.now()).build();
        testEntityManager.persist(roleChange.getUserModel());
        System.out.println(roleChange.getApprovedBy().getId());
        testEntityManager.persist(roleChange.getApprovedBy());

        // when - action or the behaviour that we are going test
        RoleChange saved = roleChangeRepository.save(roleChange);
        // then - verify the output
        assertThat(saved.getId()).isEqualTo(2L);
        assertThat(saved.getApprovedBy()).isNotNull();
        assertThat(saved.getApprovalTime()).isNotNull();
    }

}