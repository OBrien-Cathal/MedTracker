package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.dto.PractitionerRoleRequestsDTO;
import com.cathalob.medtracker.exception.PractitionerRoleRequestNotFound;
import com.cathalob.medtracker.exception.PractitionerRoleRequestValidationFailed;
import com.cathalob.medtracker.exception.UserNotFound;
import com.cathalob.medtracker.model.PractitionerRoleRequest;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.repository.PractitionerRoleRequestRepository;
import com.cathalob.medtracker.repository.UserModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements com.cathalob.medtracker.service.UserService {
    private final UserModelRepository userModelRepository;
    private final PractitionerRoleRequestRepository practitionerRoleRequestRepository;

    @Override
    public UserModel findByLogin(String username) throws UserNotFound {
        Optional<UserModel> maybeUserModel = userModelRepository.findByUsername(username);
        if (maybeUserModel.isEmpty()) throw new UserNotFound(username);
        return maybeUserModel.orElse(null);
    }

    @Override
    public List<UserModel> getUserModels() {
        return userModelRepository.findAll();
    }

    @Override
    public List<UserModel> getPatientUserModels() {
        return userModelRepository.findByRole(USERROLE.USER);
    }

    public Map<Long, UserModel> getUserModelsById() {
        return getUserModels()
                .stream().collect(Collectors.toMap(UserModel::getId, Function.identity()));
    }

    //    USER Role functions
    @Override
    public boolean submitPractitionerRoleRequest(String username) {
        return savePractitionerRoleRequest(new PractitionerRoleRequest(), findByLogin(username)) != null;
    }

    @Override
    public PractitionerRoleRequest savePractitionerRoleRequest(PractitionerRoleRequest practitionerRoleRequest, UserModel userModel)
            throws PractitionerRoleRequestValidationFailed {
        Optional<PractitionerRoleRequest> maybeExistingPractitionerRoleRequest = Optional.empty();
        try {
            maybeExistingPractitionerRoleRequest = getPractitionerRoleRequest(userModel.getId());
        } catch (PractitionerRoleRequestNotFound e) {
//            do nothing, we expect an empty prr here
        }

        if (maybeExistingPractitionerRoleRequest.isPresent()) {
            throw new PractitionerRoleRequestValidationFailed("Request already exists");
        }
        practitionerRoleRequest.setUserModel(userModel);
        return practitionerRoleRequestRepository.save(practitionerRoleRequest);
    }

    @Override
    public Optional<PractitionerRoleRequest> getPractitionerRoleRequest(String username) {
        return getPractitionerRoleRequest(findByLogin(username).getId());
    }

    @Override
    public Optional<PractitionerRoleRequest> getPractitionerRoleRequest(Long userModelId) throws PractitionerRoleRequestNotFound {
        Optional<PractitionerRoleRequest> practitionerRoleRequest = practitionerRoleRequestRepository.findById(userModelId);
        if (practitionerRoleRequest.isEmpty()) {
            throw new PractitionerRoleRequestNotFound("Request not found");
        }
        return practitionerRoleRequest;
    }

    //ADMIN user functions
    @Override
    public List<PractitionerRoleRequest> getPractitionerRoleRequests() {
        return practitionerRoleRequestRepository.findAll();
    }

    public PractitionerRoleRequestsDTO getPractitionerRoleRequestsDTO() {
        return new PractitionerRoleRequestsDTO(getPractitionerRoleRequests());
    }

    @Override
    public boolean approvePractitionerRoleRequests(List<PractitionerRoleRequest> requests) {
        validatedPractitionerRoleRequests(requests).forEach(request -> {
            log.info("processing req for: " + request.getUserModel().getUsername() + request.getUserModel().getId());
            if (request.isApproved()) {
                request.getUserModel().setRole(USERROLE.PRACT);
            } else {
                request.getUserModel().setRole(USERROLE.USER);
            }
            request.setId(request.getUserModel().getId());
            if (request.isApproved()) {
                practitionerRoleRequestRepository.save(request);
            } else {
                practitionerRoleRequestRepository.delete(request);
            }
            userModelRepository.save(request.getUserModel());
        });
        return true;
    }

    private List<PractitionerRoleRequest> validatedPractitionerRoleRequests(List<PractitionerRoleRequest> requests) throws PractitionerRoleRequestValidationFailed {
        if (requests.isEmpty()) {
            throw new PractitionerRoleRequestValidationFailed("No valid changes detected");
        }
        List<PractitionerRoleRequest> validated = new ArrayList<>();

        requests.forEach(request -> {
            String username = request.getUserModel().getUsername();
            UserModel existingUser = null;
            try {
                existingUser = findByLogin(username);
            } catch (UserNotFound userNotFound) {
                throw new PractitionerRoleRequestValidationFailed("Non existent user detected: " + username);
            }
            request.setUserModel(existingUser);
            log.info("validating req for: " + username + request.getUserModel().getId());

            try {
                getPractitionerRoleRequest(existingUser.getId());
            } catch (PractitionerRoleRequestNotFound practitionerRoleRequestNotFound) {
                throw new PractitionerRoleRequestValidationFailed("No practitioner role request found for user");
            }
            if (!(request.getUserModel().getRole().equals(USERROLE.USER)) && request.isApproved()) {
                throw new PractitionerRoleRequestValidationFailed("User does not have the correct role to upgrade: " + request.getUserModel().getUsername());
            }
            if (!(request.getUserModel().getRole().equals(USERROLE.PRACT)) && !(request.isApproved())) {
                throw new PractitionerRoleRequestValidationFailed("User does not have the correct role to downgrade: " + request.getUserModel().getUsername());
            }
            validated.add(request);
        });
        return validated;
    }

    @Override
    public boolean submitPasswordChangeRequest() {
        return false;
    }
}
