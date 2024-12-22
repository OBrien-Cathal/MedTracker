package com.cathalob.medtracker.service;

import com.cathalob.medtracker.exception.UserNotFound;
import com.cathalob.medtracker.model.PractitionerRoleRequest;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.payload.response.GenericRequestResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {


    UserModel findByLogin(String login) throws UserNotFound;

    List<UserModel> getUserModels();

    List<UserModel> getPatientUserModels();

    //  USER Role functions
    GenericRequestResponse submitRoleChange(String newRoleName, String submitterUserName);

    GenericRequestResponse approveRoleChange(Long roleChangeUserId, USERROLE newRole, String approvedByUserName);

    boolean submitPractitionerRoleRequest(String username);

    PractitionerRoleRequest savePractitionerRoleRequest(PractitionerRoleRequest practitionerRoleRequest, UserModel userModel);

    Optional<PractitionerRoleRequest> getPractitionerRoleRequest(String username);

    Optional<PractitionerRoleRequest> getPractitionerRoleRequest(Long userModelId);

    //ADMIN user functions
    List<PractitionerRoleRequest> getPractitionerRoleRequests();

    boolean approvePractitionerRoleRequests(List<PractitionerRoleRequest> requests);

    boolean submitPasswordChangeRequest();
}
