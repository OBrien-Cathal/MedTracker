package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.userroles.RoleChange;
import com.cathalob.medtracker.payload.data.RoleChangeData;
import com.cathalob.medtracker.payload.response.generic.GenericResponse;
import com.cathalob.medtracker.payload.response.rolechange.RoleChangeStatusResponse;
import com.cathalob.medtracker.repository.RoleChangeRepository;
import com.cathalob.medtracker.repository.UserModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.cathalob.medtracker.testdata.RoleChangeBuilder.aRoleChange;
import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
class UserServiceImplTests {
    @Mock
    private UserModelRepository userModelRepository;

    @Mock
    private RoleChangeRepository roleChangeRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private UserModel userModel;

    @BeforeEach
    public void setUp() {
        this.userModel = aUserModel().withId(1L).build();
    }


    @DisplayName("Get all UserModels")
    @Test
    public void givenGetUsersRequest_whenGetUserModels_thenReturnAllUserModels() {
        //given - precondition or setup
        given(userModelRepository
                .findAll()).willReturn(List.of(userModel, aUserModel().withId(2L).withUsername("username2").build()));

        // when - action or the behaviour that we are going test
        List<UserModel> userModels = userService.getUserModels();

        // then
        assertThat(userModels).isNotNull();
        assertThat(userModels.size()).isEqualTo(2);
    }



    @Test
    public void givenRoleChange_whenSubmitRoleChange_thenReturnSavedRoleChange() {
        //given - precondition or setup
        RoleChange roleChange = aRoleChange().withId(1L).build();
        USERROLE newRole = USERROLE.PRACTITIONER;

        given(userModelRepository.findByUsername(roleChange.getUserModel().getUsername())).willReturn(Optional.of(userModel));
        given(roleChangeRepository.save(any(RoleChange.class))).willReturn(roleChange);

        // when - action or the behaviour that we are going test
        GenericResponse response = userService.submitRoleChange(newRole,
                userModel.getUsername());
        // then - verify the output
        assertThat(response.getResponseInfo().isSuccessful()).isTrue();
        verify(roleChangeRepository, times(1)).save(any(RoleChange.class));
    }


    @Disabled("Validation error returned if unapproved role change exists for the same type of role and user")
    @Test
    public void givenExistingUnapprovedRoleChangeForRoleAndUser_when_then() {
        //given - precondition or setup

        // when - action or the behaviour that we are going test

        // then - verify the output
    }

    @Disabled("Validation error returned if current user role can not be changed")
    @Test
    public void givenCurrentAdminRole_when_then() {
        //given - precondition or setup

        // when - action or the behaviour that we are going test

        // then - verify the output
    }

    @Test
    public void givenRoleChanges_whenGetUnapprovedRoleChanges_thenReturnRoleChanges() {
        RoleChange roleChange = aRoleChange().withId(1L).build();
        roleChange.setUserModel(userModel);
        RoleChange roleChange2 = aRoleChange().withNewRole(USERROLE.ADMIN).withId(2L).build();
        roleChange2.getUserModel().setId(2L);


        given(roleChangeRepository.findByApprovedById(null))
                .willReturn(List.of(roleChange, roleChange2));


        // when - action or the behaviour that we are going test
        List<RoleChangeData> unapprovedRoleChanges = userService.getUnapprovedRoleChanges();
        // then - verify the output
        assertThat(unapprovedRoleChanges.size()).isEqualTo(2);
        assertThat(unapprovedRoleChanges.stream().anyMatch(roleChangeData -> roleChangeData.getUserRole().equals(USERROLE.PRACTITIONER)));
        assertThat(unapprovedRoleChanges.stream().anyMatch(roleChangeData -> roleChangeData.getUserRole().equals(USERROLE.ADMIN)));

        verify(roleChangeRepository, times(1)).findByApprovedById(null);
    }


    @Test
    public void givenExistingRoleChange_whenApproveRoleChange_thenReturnSuccessfulResponse() {
        //given - precondition or setup
        RoleChange roleChange = aRoleChange().withId(1L).build();
        roleChange.setUserModel(userModel);
        UserModel approvedBy = aUserModel().withRole(USERROLE.ADMIN).withUsername("admin").build();
        given(userModelRepository.findByUsername(approvedBy.getUsername())).willReturn(Optional.of(approvedBy));
        given(roleChangeRepository.findById(roleChange.getId()))
                .willReturn(Optional.of(roleChange));

        given(roleChangeRepository.save(roleChange)).willReturn(roleChange);
        GenericResponse expectedResponse = GenericResponse.Success();
        // when - action or the behaviour that we are going test
        GenericResponse response = userService.approveRoleChange(roleChange.getId(), approvedBy.getUsername());
        // then - verify the output
        assertThat(response.getResponseInfo().isSuccessful()).isTrue();
        assertThat(response.getResponseInfo().getMessage()).isEqualTo(expectedResponse.getResponseInfo().getMessage());
        assertThat(response.getResponseInfo().getErrors()).size().isEqualTo(0);
        verify(roleChangeRepository, times(1)).save(any(RoleChange.class));
    }

    @Test
    public void givenExistingApprovedPractitionerRoleChange_whenGetRoleChangeStatus_thenReturnRoleChangeStatus() {
        RoleChangeStatusResponse roleChangeStatus = roleChangeStatusMatches(
                USERROLE.PRACTITIONER,
                true);
        // then - verify the output
        System.out.println(roleChangeStatus);
        assertThat(roleChangeStatus.getAdminRoleChange().getId()).isEqualTo(null);
        assertThat(roleChangeStatus.getAdminRoleChange().getStatus()).isEqualTo("Unrequested");
        assertThat(roleChangeStatus.getPractitionerRoleChange().getId()).isEqualTo(1L);
        assertThat(roleChangeStatus.getPractitionerRoleChange().getStatus()).isEqualTo("Approved");
        verify(roleChangeRepository, times(1)).findByUserModelId(1L);
    }

    @Test
    public void givenExistingUnapprovedPractitionerRoleChange_whenGetRoleChangeStatus_thenReturnRoleChangeStatus() {
        RoleChangeStatusResponse roleChangeStatus = roleChangeStatusMatches(
                USERROLE.PRACTITIONER,
                false);
        // then - verify the output
        System.out.println(roleChangeStatus);
        assertThat(roleChangeStatus.getAdminRoleChange().getId()).isEqualTo(null);
        assertThat(roleChangeStatus.getAdminRoleChange().getStatus()).isEqualTo("Unrequested");
        assertThat(roleChangeStatus.getPractitionerRoleChange().getId()).isEqualTo(1L);
        assertThat(roleChangeStatus.getPractitionerRoleChange().getStatus()).isEqualTo("Pending");
        verify(roleChangeRepository, times(1)).findByUserModelId(1L);
    }

    @Test
    public void givenExistingApprovedAdminRoleChange_whenGetRoleChangeStatus_thenReturnRoleChangeStatus() {
        RoleChangeStatusResponse roleChangeStatus = roleChangeStatusMatches(
                USERROLE.ADMIN,
                true);
        // then - verify the output
        System.out.println(roleChangeStatus);
        assertThat(roleChangeStatus.getAdminRoleChange().getId()).isEqualTo(1L);
        assertThat(roleChangeStatus.getAdminRoleChange().getStatus()).isEqualTo("Approved");
        assertThat(roleChangeStatus.getPractitionerRoleChange().getId()).isEqualTo(null);
        assertThat(roleChangeStatus.getPractitionerRoleChange().getStatus()).isEqualTo("Unrequested");
        verify(roleChangeRepository, times(1)).findByUserModelId(1L);
    }

    @Test
    public void givenExistingUnapprovedAdminRoleChange_whenGetRoleChangeStatus_thenReturnRoleChangeStatus() {
        RoleChangeStatusResponse roleChangeStatus = roleChangeStatusMatches(
                USERROLE.ADMIN,
                false);

        // then - verify the output
        System.out.println(roleChangeStatus);
        assertThat(roleChangeStatus.getAdminRoleChange().getId()).isEqualTo(1L);
        assertThat(roleChangeStatus.getAdminRoleChange().getStatus()).isEqualTo("Pending");
        assertThat(roleChangeStatus.getPractitionerRoleChange().getId()).isEqualTo(null);
        assertThat(roleChangeStatus.getPractitionerRoleChange().getStatus()).isEqualTo("Unrequested");
        verify(roleChangeRepository, times(1)).findByUserModelId(1L);
    }

    private RoleChangeStatusResponse roleChangeStatusMatches(USERROLE newRole, boolean approved) {
        //given - precondition or setup
        RoleChange roleChange = aRoleChange().withId(1L).build();
        roleChange.setNewRole(newRole);
        roleChange.setUserModel(userModel);
        given(userModelRepository.findByUsername(userModel.getUsername())).willReturn(Optional.of(userModel));
        System.out.println(roleChange);
        if (approved) {
            UserModel approvedBy = aUserModel().withRole(USERROLE.ADMIN).withUsername("admin").build();
            roleChange.setApprovedBy(approvedBy);
            roleChange.setApprovalTime(LocalDateTime.now());
        }
        given(roleChangeRepository.findByUserModelId(userModel.getId()))
                .willReturn(List.of(roleChange));

        // when - action or the behaviour that we are going test
        return userService.getRoleChangeStatus(userModel.getUsername());
    }

    @DisplayName("Failed password change request - unimplemented")
    @Test
    public void givenUserRequest_whenSubmitPasswordChangeRequest_thenReturnFalse() {
        //given - No setup

        // when - action or the behaviour that we are going test
        boolean requestStatus = userService.submitPasswordChangeRequest();
        // then - verify the output
        assertThat(requestStatus).isFalse();
    }




}