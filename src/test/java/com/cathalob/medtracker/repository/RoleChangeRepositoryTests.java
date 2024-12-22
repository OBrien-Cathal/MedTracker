package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.userroles.RoleChange;
import com.cathalob.medtracker.testdata.UserModelBuilder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static com.cathalob.medtracker.testdata.RoleChangeBuilder.aRoleChange;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RoleChangeRepositoryTests {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private RoleChangeRepository roleChangeRepository;

    @Test

    public void givenRoleChange_whenSave_thenReturnSavedRoleChange() {
        //given - precondition or setup
        RoleChange roleChange = aRoleChange().build();
        testEntityManager.persist(roleChange.getUserModel());
        // when - action or the behaviour that we are going test
        RoleChange saved = roleChangeRepository.save(roleChange);

        // then - verify the output
        assertThat(saved.getId()).isGreaterThan(0L);
        assertThat(saved.getApprovedBy()).isNull();
        assertThat(saved.getApprovalTime()).isNull();
    }

    @Test

    public void givenApprovedRoleChange_whenSave_thenReturnSavedRoleChange() {
        //given - precondition or setup

        UserModelBuilder approvedBy = UserModelBuilder.aUserModel().withRole(USERROLE.ADMIN);
        RoleChange roleChange = aRoleChange().withApprovedByUserModelBuilder(approvedBy).withApprovalTime(LocalDateTime.now()).build();
        testEntityManager.persist(roleChange.getUserModel());
        testEntityManager.persist(roleChange.getApprovedBy());

        // when - action or the behaviour that we are going test
        RoleChange saved = roleChangeRepository.save(roleChange);
        // then - verify the output
        assertThat(saved.getId()).isGreaterThan(0L);
        assertThat(saved.getApprovedBy()).isNotNull();
        assertThat(saved.getApprovalTime()).isNotNull();
    }


    @Test
    public void givenPersistedUnapprovedRoleChanges_whenFindUnapprovedRoleChange_thenReturnOnlyOne() {
        //given - precondition or setup
        RoleChange roleChange = aRoleChange().build();
        UserModel roleChangeUserModel = roleChange.getUserModel();
        testEntityManager.persist(roleChangeUserModel);
        testEntityManager.persist(roleChange);

        UserModel approvedBy = UserModelBuilder.aUserModel().withRole(USERROLE.ADMIN).withUsername("admin").build();
        testEntityManager.persist(approvedBy);

        RoleChange anotherRoleChange = aRoleChange()
                .withUserModelBuilder(UserModelBuilder.aUserModel().withUsername("other"))
                .build();
        testEntityManager.persist(anotherRoleChange.getUserModel());
        testEntityManager.persist(anotherRoleChange);
        testEntityManager.flush();
        // when - action or the behaviour that we are going test
        List<RoleChange> unapprovedRoleChanges = roleChangeRepository.findUnapprovedRoleChange(roleChangeUserModel.getId(), USERROLE.PRACTITIONER);
        // then - verify the output
        assertThat(unapprovedRoleChanges.size()).isEqualTo(1);

    }


    @Test
    public void givenPersistedUnapprovedRoleChange_whenFindByApprovedByNull_thenReturnOnlyUnapprovedRoleChanges() {
        //given - precondition or setup
        RoleChange roleChange = aRoleChange().build();
        String unapprovedRoleChangeUser = "unapproved";
        RoleChange otherRoleChange = aRoleChange()
                .withUserModelBuilder(UserModelBuilder.aUserModel().withUsername(unapprovedRoleChangeUser))
                .build();

        UserModel roleChangeUserModel = roleChange.getUserModel();
        testEntityManager.persist(roleChangeUserModel);
        testEntityManager.persist(otherRoleChange.getUserModel());

        UserModel approvedBy = UserModelBuilder.aUserModel().withRole(USERROLE.ADMIN).withUsername("admin").build();
        testEntityManager.persist(approvedBy);


        roleChange.setApprovedBy(approvedBy);
        roleChange.setApprovalTime(LocalDateTime.now());
        testEntityManager.persist(roleChange);
//        otherRoleChange.setApprovedBy(approvedBy);
//        otherRoleChange.setApprovalTime(LocalDateTime.now());
        testEntityManager.persist(otherRoleChange);
        System.out.println(otherRoleChange.getUserModel().getUsername());
        testEntityManager.flush();
        // when - action or the behaviour that we are going test
        List<RoleChange> unapprovedRoleChanges = roleChangeRepository.findByApprovedById(null);
        // then - verify the output
        assertThat(unapprovedRoleChanges.size()).isEqualTo(1);
        assertThat(unapprovedRoleChanges).allMatch(roleChange1 -> roleChange1.getApprovedBy() == null);
        assertThat(unapprovedRoleChanges).allMatch(roleChange1 -> roleChange1.getUserModel().getUsername().equals(unapprovedRoleChangeUser));

    }

    @DisplayName("Test retrieval of unapproved role requests for specific user and role ")
    @Test
    public void givenPersistedUnapprovedRoleChanges_whenFindByUserModelIdAndNewRoleAndApprovedById_thenReturnOnlyUnapprovedRoleChangesForUser() {
        //given - precondition or setup
        RoleChange practitionerRoleChange = aRoleChange().build();
        String otherRoleChangeUser = "other";
        RoleChange otherUserRoleChange = aRoleChange()
                .withUserModelBuilder(UserModelBuilder.aUserModel().withUsername(otherRoleChangeUser))
                .build();
        RoleChange adminRoleChange = aRoleChange().withNewRole(USERROLE.ADMIN).build();

        UserModel roleChangeUserModel = practitionerRoleChange.getUserModel();
        adminRoleChange.setUserModel(roleChangeUserModel);

        testEntityManager.persist(roleChangeUserModel);
        testEntityManager.persist(otherUserRoleChange.getUserModel());

        UserModel approvedBy = UserModelBuilder.aUserModel().withRole(USERROLE.ADMIN).withUsername("admin").build();
        testEntityManager.persist(approvedBy);


//        practitionerRoleChange.setApprovedBy(approvedBy);
//        practitionerRoleChange.setApprovalTime(LocalDateTime.now());
        testEntityManager.persist(practitionerRoleChange);
        testEntityManager.persist(adminRoleChange);

//        otherUserRoleChange.setApprovedBy(approvedBy);
//        otherUserRoleChange.setApprovalTime(LocalDateTime.now());
        testEntityManager.persist(otherUserRoleChange);
        System.out.println(otherUserRoleChange.getUserModel().getUsername());
        testEntityManager.flush();
        // when - action or the behaviour that we are going test
        List<RoleChange> unapprovedRoleChangesToPractitioner = roleChangeRepository.findByUserModelIdAndNewRoleAndApprovedById(roleChangeUserModel.getId(), USERROLE.PRACTITIONER, null);
        // then - verify the output
        assertThat(unapprovedRoleChangesToPractitioner.size()).isEqualTo(1);
        assertThat(unapprovedRoleChangesToPractitioner).allMatch(roleChange1 -> roleChange1.getApprovedBy() == null);
        assertThat(unapprovedRoleChangesToPractitioner).allMatch(roleChange1 -> roleChange1.getNewRole() == USERROLE.PRACTITIONER);
        assertThat(unapprovedRoleChangesToPractitioner).allMatch(roleChange1 -> roleChange1.getUserModel().getUsername().equals(roleChangeUserModel.getUsername()));

    }


    @Test
    public void givenPersistedUnapprovedRoleChange_whenFindAll_then() {
        //given - precondition or setup
        RoleChange roleChange = aRoleChange().build();
        testEntityManager.persist(roleChange.getUserModel());
        testEntityManager.persist(roleChange);

        UserModel approvedBy = UserModelBuilder.aUserModel().withRole(USERROLE.ADMIN).withUsername("admin").build();
        testEntityManager.persist(approvedBy);

        RoleChange anotherRoleChange = aRoleChange()
                .withUserModelBuilder(UserModelBuilder.aUserModel().withUsername("other"))
                .build();
        testEntityManager.persist(anotherRoleChange.getUserModel());
        testEntityManager.persist(anotherRoleChange);
        testEntityManager.flush();
        // when - action or the behaviour that we are going test

        List<RoleChange> unapprovedRoleChanges = roleChangeRepository.findAll();
        // then - verify the output
        assertThat(unapprovedRoleChanges.size()).isEqualTo(2);
    }


}