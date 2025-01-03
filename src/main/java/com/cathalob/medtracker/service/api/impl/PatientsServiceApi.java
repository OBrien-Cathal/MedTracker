package com.cathalob.medtracker.service.api.impl;

import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.userroles.RoleChange;
import com.cathalob.medtracker.payload.data.PatientRegistrationData;
import com.cathalob.medtracker.payload.response.PatientRegistrationResponse;
import com.cathalob.medtracker.repository.PatientRegistrationRepository;
import com.cathalob.medtracker.repository.RoleChangeRepository;
import com.cathalob.medtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class PatientsServiceApi {
    private final UserService userService;
    private final RoleChangeRepository roleChangeRepository;
    private final PatientRegistrationRepository patientRegistrationRepository;

    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    public List<UserModel> getPatientUserModels(String username) {
        UserModel userModel = userService.findByLogin(username);
        if (userModel == null || !userModel.getRole().equals(USERROLE.PRACTITIONER)) return List.of();
        List<Long> patientUserModelIds = patientRegistrationRepository.findByPractitionerUserModel(userModel)
                .stream()
                .map((patientRegistration -> patientRegistration.getUserModel().getId())).toList();
        return userService.findUserModelsById(patientUserModelIds);
    }


    public PatientRegistrationResponse registerPatient(String username, Long practitionerId) {
        UserModel toRegister = userService.findByLogin(username);
        Optional<UserModel> maybePractitioner = userService.findUserModelById(practitionerId);

        PatientRegistrationResponse patientRegistrationResponse = new PatientRegistrationResponse();

        if (maybePractitioner.isEmpty()) {
            patientRegistrationResponse.setMessage("Registration failed");
            patientRegistrationResponse.setErrors(List.of("Practitioner does not exist"));
            return patientRegistrationResponse;
        }

        PatientRegistration patientRegistration = new PatientRegistration();
        patientRegistration.setUserModel(toRegister);
        patientRegistration.setPractitionerUserModel(maybePractitioner.get());


        RoleChange roleChange = new RoleChange();
        roleChange.setNewRole(USERROLE.PATIENT);
        roleChange.setUserModel(toRegister);
        roleChange.setOldRole(toRegister.getRole());
        roleChange.setRequestTime(LocalDateTime.now());

        List<String> errors = validateRoleChangeSubmission(roleChange);
        if (!errors.isEmpty()) {
            patientRegistrationResponse.setMessage("Registration failed");
            patientRegistrationResponse.setErrors(errors);
            return patientRegistrationResponse;
        }

        roleChangeRepository.save(roleChange);
        patientRegistration.setRoleChange(roleChange);
        PatientRegistration saved = patientRegistrationRepository.save(patientRegistration);

        patientRegistrationResponse.setMessage("Registration Pending");
        PatientRegistrationData patientRegistrationData = new PatientRegistrationData(
                saved.getId(),
                practitionerId,
                false);
        patientRegistrationResponse.setData(patientRegistrationData);
        System.out.println(patientRegistrationResponse.getData().getId());
        return patientRegistrationResponse;

    }


    public List<PatientRegistration> getPatientRegistrations(String practitionerUsername) {
        return patientRegistrationRepository.findByPractitionerUserModel(userService.findByLogin(practitionerUsername));
    }

    private List<String> validateRoleChangeSubmission(RoleChange roleChange) {
        ArrayList<String> errors = new ArrayList<>();
        List<RoleChange> unapproved = roleChangeRepository.findByUserModelIdAndNewRoleAndApprovedById(
                roleChange.getUserModel().getId(),
                roleChange.getNewRole(),
                null);
        if (roleChange.getUserModel().getRole() != USERROLE.USER) {
            errors.add(String.format("Current User Role: %s is not a candidate for role change to: %s",
                    roleChange.getUserModel().getRole().name(),
                    roleChange.getNewRole()));
        }
        if (!unapproved.isEmpty())
            errors.add("Unapproved request already submitted for role: " + roleChange.getNewRole().name());
        return errors;
    }
}
