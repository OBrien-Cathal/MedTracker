package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.exception.validation.PatientRegistrationException;
import com.cathalob.medtracker.fileupload.BloodPressureFileImporter;
import com.cathalob.medtracker.fileupload.DoseFileImporter;
import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.factories.PatientRegistrationFactory;
import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.model.userroles.RoleChange;
import com.cathalob.medtracker.payload.data.PatientRegistrationData;
import com.cathalob.medtracker.payload.response.patient.ApprovePatientRegistrationResponse;
import com.cathalob.medtracker.payload.response.patient.PatientRegistrationResponse;
import com.cathalob.medtracker.repository.PatientRegistrationRepository;
import com.cathalob.medtracker.repository.RoleChangeRepository;
import com.cathalob.medtracker.service.UserService;
import com.cathalob.medtracker.validate.model.RegisterPatientValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class PatientsService {
    private final UserService userService;
    private final RoleChangeRepository roleChangeRepository;
    private final PatientRegistrationRepository patientRegistrationRepository;
    private final PrescriptionsService prescriptionsService;
    private final BloodPressureDataService bloodPressureDataService;
    private final DoseService doseService;
    private final EvaluationDataService evaluationDataService;

    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    public List<UserModel> getPatientUserModelsForPractitioner(String username) {
        UserModel userModel = userService.findByLogin(username);
        if (userModel == null || !userModel.getRole().equals(USERROLE.PRACTITIONER)) return List.of();
        return patientRegistrationRepository.findByPractitionerUserModel(userModel)
                .stream()
                .map((PatientRegistration::getUserModel)).toList();
    }


    public PatientRegistrationResponse registerPatient(String username, Long practitionerId) throws PatientRegistrationException {
        UserModel toRegister = userService.findByLogin(username);
        Optional<UserModel> maybePractitioner = userService.findUserModelById(practitionerId);
        UserModel practitioner = maybePractitioner.orElse(null);

        PatientRegistration patientRegistration = PatientRegistrationFactory.PatientRegistration(toRegister, practitioner, false);

        RegisterPatientValidator val = RegisterPatientValidator.aRegisterPatientValidator();
        val.validateRegisterPatient(patientRegistration, patientRegistrationRepository.findByUserModelAndPractitionerUserModel(toRegister, practitioner));
        if (val.validationFailed()) throw new PatientRegistrationException(val.getErrors());

        PatientRegistration saved = patientRegistrationRepository.save(patientRegistration);

        return PatientRegistrationResponse.Success(saved);
    }


    public ApprovePatientRegistrationResponse approvePatientRegistration(String username, Long patientRegistrationId) {
        UserModel shouldBePractitionerUserModel = userService.findByLogin(username);
        Optional<PatientRegistration> reg = patientRegistrationRepository.findById(patientRegistrationId);

        ArrayList<String> errors = new ArrayList<>();
        if (!shouldBePractitionerUserModel.getRole().equals(USERROLE.PRACTITIONER)) {
            errors.add("Only users with PRACTITIONER role can approve patient registrations");
            return ApprovePatientRegistrationResponse.Failed(patientRegistrationId, errors);
        }
        if (reg.isEmpty()) {
            errors.add("Registration with id " + patientRegistrationId + " does not exist");
            return ApprovePatientRegistrationResponse.Failed(patientRegistrationId, errors);
        }
        PatientRegistration patientRegistration = reg.get();
        if (!shouldBePractitionerUserModel.getId().equals(patientRegistration.getPractitionerUserModel().getId())) {
            errors.add("Approval of requests for other users not allowed");
            return ApprovePatientRegistrationResponse.Failed(patientRegistrationId, errors);
        }
        if (patientRegistration.isRegistered()) {
            errors.add("Registration is already approved for reg id: " + patientRegistrationId);
            return ApprovePatientRegistrationResponse.Failed(patientRegistrationId, errors);
        }
        if (!List.of(USERROLE.USER, USERROLE.PATIENT).contains(patientRegistration.getUserModel().getRole())) {
            errors.add("Cannot approve registration of user with role: " + patientRegistration.getUserModel().getRole());
            return ApprovePatientRegistrationResponse.Failed(patientRegistrationId, errors);
        }

        UserModel toRegister = patientRegistration.getUserModel();
        if (toRegister.getRole().equals(USERROLE.USER)) {
//            Add role change if this is the first time a user has requested a patient registration
            RoleChange roleChange = new RoleChange();
            roleChange.setNewRole(USERROLE.PATIENT);
            roleChange.setUserModel(toRegister);
            roleChange.setOldRole(toRegister.getRole());
            roleChange.setRequestTime(LocalDateTime.now());
            roleChange.setApprovedBy(patientRegistration.getPractitionerUserModel());
            roleChange.setApprovalTime(LocalDateTime.now());
            roleChangeRepository.save(roleChange);
        }

        patientRegistration.setRegistered(true);
        patientRegistrationRepository.save(patientRegistration);
        return ApprovePatientRegistrationResponse.Success(patientRegistration.getId());
    }


    public List<PatientRegistrationData> getPatientRegistrations(String username) {
        UserModel userModel = userService.findByLogin(username);
        List<PatientRegistration> patientRegistrations = new ArrayList<>();
        if (userModel.getRole().equals(USERROLE.PRACTITIONER)) {
            patientRegistrations = patientRegistrationRepository.findByPractitionerUserModel(userModel);
        }
        if (userModel.getRole().equals(USERROLE.PATIENT) || userModel.getRole().equals(USERROLE.USER)) {
            patientRegistrations = patientRegistrationRepository.findByUserModel(userModel);
        }
        return patientRegistrations.stream().map((PatientRegistrationData::From
        )).toList();
    }

    public void importDoseFile(MultipartFile file, String username) {
        new DoseFileImporter(
                userService.findByLogin(username),
                evaluationDataService,
                prescriptionsService,
                doseService)
                .processMultipartFile(file);
    }

    public void importBloodPressureFile(MultipartFile file, String username) {
        new BloodPressureFileImporter(
                userService.findByLogin(username),
                evaluationDataService,
                this)
                .processMultipartFile(file);
    }

    public void saveBloodPressureReadings(List<BloodPressureReading> bloodPressureReadings) {
        bloodPressureDataService.saveBloodPressureReadings(bloodPressureReadings);
    }

}
