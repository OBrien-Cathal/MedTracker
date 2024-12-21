package com.cathalob.medtracker.service;

import com.cathalob.medtracker.exception.PractitionerRoleRequestValidationFailed;
import com.cathalob.medtracker.exception.UserNotFound;
import com.cathalob.medtracker.model.PractitionerRoleRequest;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.userroles.RoleChange;
import com.cathalob.medtracker.repository.PractitionerRoleRequestRepository;
import com.cathalob.medtracker.repository.RoleChangeRepository;
import com.cathalob.medtracker.repository.UserModelRepository;
import com.cathalob.medtracker.service.impl.UserServiceImpl;
import com.cathalob.medtracker.testdata.RoleChangeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.cathalob.medtracker.testdata.RoleChangeBuilder.aRoleChange;
import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTests {
    @Mock
    private UserModelRepository userModelRepository;
    @Mock
    private PractitionerRoleRequestRepository practitionerRoleRequestRepository;
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

    @DisplayName("Save practitioner role request")
    @Test
    public void givenPractitionerRoleRequest_whenSavePractitionerRoleRequest_thenReturnSavedPractitionerRoleRequest() {
        //given - precondition or setup
        UserModel userModel = aUserModel().withId(1L).build();
        PractitionerRoleRequest practitionerRoleRequest = new PractitionerRoleRequest();

//        given(userModelRepository.findByUsername(userModel.getUsername())).willReturn(Optional.of(userModel));
        given(practitionerRoleRequestRepository.findById(userModel.getId())).willReturn(Optional.empty());
        given(practitionerRoleRequestRepository.save(practitionerRoleRequest)).willReturn(practitionerRoleRequest);

        // when - action or the behaviour that we are going test
        PractitionerRoleRequest savedPractitionerRoleRequest = userService.savePractitionerRoleRequest(practitionerRoleRequest, userModel);
        // then - verify the output
        assertThat(savedPractitionerRoleRequest.getUserModel()).isEqualTo(userModel);
        verify(practitionerRoleRequestRepository, times(1)).save(practitionerRoleRequest);
    }

    @DisplayName("Fail practitioner role request submit due to already existing request")
    @Test
    public void givenExistingPractitionerRoleRequest_whenSubmitPractitionerRoleRequest_thenThrow() {
        //given - precondition or setup
        UserModel userModel = aUserModel().withId(1L).build();
        PractitionerRoleRequest existingPractitionerRoleRequest = new PractitionerRoleRequest();
        existingPractitionerRoleRequest.setId(1L);

        given(userModelRepository.findByUsername(userModel.getUsername())).willReturn(Optional.of(userModel));
        given(practitionerRoleRequestRepository.findById(userModel.getId())).willReturn(Optional.of(existingPractitionerRoleRequest));

        // when - action or the behaviour that we are going test
        assertThrows(PractitionerRoleRequestValidationFailed.class,
                () -> userService.submitPractitionerRoleRequest(userModel.getUsername()));

        // then
        verify(practitionerRoleRequestRepository, never()).save(any(PractitionerRoleRequest.class));
    }

    @DisplayName("Fail practitioner role request submit due to non existent user")
    @Test
    public void givenPractitionerRoleRequestForNonExistentUser_whenSubmitPractitionerRoleRequest_thenThrow() {
        //given - precondition or setup
        UserModel userModel = aUserModel().withId(1L).build();
        given(userModelRepository.findByUsername(userModel.getUsername())).willReturn(Optional.empty());

        // when - action or the behaviour that we are going test
        assertThrows(UserNotFound.class,
                () -> userService.submitPractitionerRoleRequest(userModel.getUsername()));

        // then
        verify(practitionerRoleRequestRepository, never()).save(any(PractitionerRoleRequest.class));
    }

    @DisplayName("Approve role Upgrade")
    @Test
    public void givenListOfPractitionerRoleRequestsToApproveWithUpgrade_whenApprovePractitionerRoleRequests_thenReturnTrue() {
        //given - precondition or setup
        PractitionerRoleRequest practitionerRoleRequest = new PractitionerRoleRequest();
        UserModel userModel = aUserModel().withId(1L).build();
        practitionerRoleRequest.setUserModel(userModel);
        practitionerRoleRequest.setApproved(true);
        given(userModelRepository.findByUsername(userModel.getUsername())).willReturn(Optional.of(userModel));
        given(practitionerRoleRequestRepository.save(practitionerRoleRequest)).willReturn(practitionerRoleRequest);
        given(practitionerRoleRequestRepository.findById(userModel.getId())).willReturn(Optional.of(practitionerRoleRequest));

        // when - action or the behaviour that we are going test
        boolean areRequestsApproved = userService.approvePractitionerRoleRequests(List.of(practitionerRoleRequest));
        // then - verify the output
        assertThat(areRequestsApproved).isTrue();
    }

    @DisplayName("Approve role Downgrade")
    @Test
    public void givenListOfPractitionerRoleRequestsToApproveWithDowngrade_whenApprovePractitionerRoleRequests_thenReturnTrue() {
        //given - precondition or setup
        PractitionerRoleRequest practitionerRoleRequest = new PractitionerRoleRequest();
        UserModel userModel = aUserModel().withId(1L).withRole(USERROLE.PRACTITIONER).build();
        practitionerRoleRequest.setUserModel(userModel);
        practitionerRoleRequest.setApproved(false);
        given(userModelRepository.findByUsername(userModel.getUsername())).willReturn(Optional.of(userModel));
        given(practitionerRoleRequestRepository.findById(userModel.getId())).willReturn(Optional.of(practitionerRoleRequest));

        // when - action or the behaviour that we are going test
        boolean areRequestsApproved = userService.approvePractitionerRoleRequests(List.of(practitionerRoleRequest));
        // then - verify the output
        assertThat(areRequestsApproved).isTrue();
    }

    @DisplayName("Fail to Downgrade due to wrong current user role")
    @Test
    public void givenListOfPractitionerRoleRequestsToApproveWithInvalidDowngrade_whenApprovePractitionerRoleRequests_thenReturnTrue() {
        //given - precondition or setup
        PractitionerRoleRequest practitionerRoleRequest = new PractitionerRoleRequest();
        UserModel userModel = aUserModel().withId(1L).build();
        practitionerRoleRequest.setUserModel(userModel);
        practitionerRoleRequest.setApproved(false);
        given(userModelRepository.findByUsername(userModel.getUsername())).willReturn(Optional.of(userModel));
        given(practitionerRoleRequestRepository.findById(userModel.getId())).willReturn(Optional.of(practitionerRoleRequest));

        // when
        assertEquals("User does not have the correct role to downgrade: " + userModel.getUsername(),
                assertThrows(PractitionerRoleRequestValidationFailed.class,
                        () -> userService.approvePractitionerRoleRequests(List.of(practitionerRoleRequest))).getMessage());

        // then
        verify(practitionerRoleRequestRepository, never()).save(any(PractitionerRoleRequest.class));
        verify(practitionerRoleRequestRepository, never()).delete(any(PractitionerRoleRequest.class));
    }

    @DisplayName("Fail to Upgrade due to wrong current user role")
    @Test
    public void givenListOfPractitionerRoleRequestsToApproveWithInvalidUpgrade_whenApprovePractitionerRoleRequests_thenReturnTrue() {
        //given - precondition or setup
        PractitionerRoleRequest practitionerRoleRequest = new PractitionerRoleRequest();
        UserModel userModel = aUserModel().withId(1L).withRole(USERROLE.PRACTITIONER).build();
        practitionerRoleRequest.setUserModel(userModel);
        practitionerRoleRequest.setApproved(true);
        given(userModelRepository.findByUsername(userModel.getUsername())).willReturn(Optional.of(userModel));
        given(practitionerRoleRequestRepository.findById(userModel.getId())).willReturn(Optional.of(practitionerRoleRequest));

        // when
        assertEquals("User does not have the correct role to upgrade: " + userModel.getUsername(),
                assertThrows(PractitionerRoleRequestValidationFailed.class,
                        () -> userService.approvePractitionerRoleRequests(List.of(practitionerRoleRequest))).getMessage());

        // then
        verify(practitionerRoleRequestRepository, never()).save(any(PractitionerRoleRequest.class));
        verify(practitionerRoleRequestRepository, never()).delete(any(PractitionerRoleRequest.class));
    }

    @Test
    public void givenRoleChange_whenSubmitRoleChange_thenReturnSavedRoleChange() {
        //given - precondition or setup
        RoleChange roleChange = aRoleChange().withId(1L).build();
        given(userModelRepository.findByUsername(roleChange.getUserModel().getUsername())).willReturn(Optional.of(userModel));
        given(roleChangeRepository.save(roleChange)).willReturn(roleChange);

        // when - action or the behaviour that we are going test
        RoleChange submittedRoleChange = userService.submitRoleChange(roleChange, userModel.getUsername());
        // then - verify the output
        assertThat(submittedRoleChange.getUserModel()).isEqualTo(userModel);
        verify(roleChangeRepository, times(1)).save(roleChange);
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