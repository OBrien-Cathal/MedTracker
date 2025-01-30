package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.exception.validation.PatientRegistrationException;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.payload.data.PatientRegistrationData;
import com.cathalob.medtracker.payload.request.patient.ApprovePatientRegistrationRequest;
import com.cathalob.medtracker.payload.request.patient.PatientRegistrationRequest;
import com.cathalob.medtracker.payload.response.generic.GenericResponse;
import com.cathalob.medtracker.payload.response.patient.ApprovePatientRegistrationResponse;
import com.cathalob.medtracker.payload.response.patient.PatientRegistrationResponse;
import com.cathalob.medtracker.service.impl.PatientsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientsController {

    private final PatientsService patientsService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    public ResponseEntity<List<UserModel>> getPatientUserModels(Authentication authentication) {
        return ResponseEntity.ok(patientsService.getPatientUserModelsForPractitioner(authentication.getName()));
    }

    @GetMapping("/registrations")
    public ResponseEntity<List<PatientRegistrationData>> getPatientRegistrations(Authentication authentication) {
        return ResponseEntity.ok(patientsService.getPatientRegistrations(authentication.getName()));
    }

    @PostMapping("/registrations/submit")
    public ResponseEntity<PatientRegistrationResponse> registerPatient(
            Authentication authentication,
            @RequestBody PatientRegistrationRequest request) {
        PatientRegistrationResponse response;
        try {
            response = patientsService.registerPatient(authentication.getName(), request.getPractitionerId());
        } catch (PatientRegistrationException e) {
            response = PatientRegistrationResponse.Failed(e.getErrors());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/registrations/approve")
    public ResponseEntity<ApprovePatientRegistrationResponse> approvePatientRegistration(Authentication authentication, @RequestBody ApprovePatientRegistrationRequest request) {
        return ResponseEntity.ok(patientsService.approvePatientRegistration(authentication.getName(), request.getPatientRegistrationId()));
    }

    @PostMapping("/upload/dose-upload")
    public ResponseEntity<GenericResponse> reapDoseDataFromExcelUpload(@RequestParam("dosesFile") MultipartFile reapExcelDataFile, Authentication authentication) {
        patientsService.importDoseFile(reapExcelDataFile, (authentication.getName()));
        return ResponseEntity.ok(GenericResponse.Success());
    }

    @PostMapping("/upload/blood-pressure-upload")
    public ResponseEntity<GenericResponse> reapBloodPressureDataFromExcelUpload(@RequestParam("bloodPressureFile") MultipartFile reapExcelDataFile, Authentication authentication) {
        patientsService.importBloodPressureFile(reapExcelDataFile, (authentication.getName()));
        return ResponseEntity.ok(GenericResponse.Success());
    }
}
