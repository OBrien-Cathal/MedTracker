package com.cathalob.medtracker.service;

import com.cathalob.medtracker.exception.UserNotFound;
import com.cathalob.medtracker.model.PractitionerRoleRequest;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.payload.data.RoleChangeData;
import com.cathalob.medtracker.payload.response.GenericRequestResponse;
import com.cathalob.medtracker.payload.response.PatientRegistrationResponse;
import com.cathalob.medtracker.payload.response.RoleChangeStatusResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {


    UserModel findByLogin(String login) throws UserNotFound;

    List<UserModel> getUserModels();

    List<UserModel> getPatientUserModels(String username);


    List<UserModel> getPractitionerUserModels();

    //  USER Role functions
    //    NEW ROLE functions
    GenericRequestResponse submitRoleChange(USERROLE newRole, String submitterUserName);

    GenericRequestResponse approveRoleChange(Long roleChangeId, String approvedByUserName);

    RoleChangeStatusResponse getRoleChangeStatus(String username);

    List<RoleChangeData> getUnapprovedRoleChanges();

    //    Patient registration
    PatientRegistrationResponse registerPatient(String username, Long practitionerId);

    boolean submitPractitionerRoleRequest(String username);

    PractitionerRoleRequest savePractitionerRoleRequest(PractitionerRoleRequest practitionerRoleRequest, UserModel userModel);

    Optional<PractitionerRoleRequest> getPractitionerRoleRequest(String username);

    Optional<PractitionerRoleRequest> getPractitionerRoleRequest(Long userModelId);

    //ADMIN user functions
    List<PractitionerRoleRequest> getPractitionerRoleRequests();

    boolean approvePractitionerRoleRequests(List<PractitionerRoleRequest> requests);

    boolean submitPasswordChangeRequest();
}
