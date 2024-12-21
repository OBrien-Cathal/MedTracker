package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.userroles.RoleChange;
import com.cathalob.medtracker.testdata.UserModelBuilder;
import org.junit.jupiter.api.Test;
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
@Sql(scripts = "classpath:clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class RoleChangeRepositoryTests {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private RoleChangeRepository roleChangeRepository;

      @Test
          public void givenRoleChange_whenSave_thenReturnSavedRoleChange(){
              //given - precondition or setup
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
          public void givenApprovedRoleChange_whenSave_thenReturnSavedRoleChange(){
              //given - precondition or setup
          UserModelBuilder approvedBy = UserModelBuilder.aUserModel().withRole(USERROLE.ADMIN);
          RoleChange roleChange = aRoleChange().withApprovedByUserModelBuilder(approvedBy).withApprovalTime(LocalDateTime.now()).build();
          testEntityManager.persist(roleChange.getUserModel());
          System.out.println(roleChange.getApprovedBy().getId());
          testEntityManager.persist(roleChange.getApprovedBy());

          // when - action or the behaviour that we are going test
          RoleChange saved = roleChangeRepository.save(roleChange);
          // then - verify the output
          assertThat(saved.getId()).isEqualTo(1L);
          assertThat(saved.getApprovedBy()).isNotNull();
          assertThat(saved.getApprovalTime()).isNotNull();
          }

}