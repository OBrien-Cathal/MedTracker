package com.cathalob.medtracker.controller.api;

import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientsControllerApi {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    public ResponseEntity<List<UserModel>> getPatientUserModels(Authentication authentication) {
        return ResponseEntity.ok(userService.getPatientUserModels(authentication.getName()));
    }
    @GetMapping("/registrations")
    public ResponseEntity<List<PatientRegistration>> getPatientRegistrations(Authentication authentication) {
        return ResponseEntity.ok(userService.getPatientRegistrations(authentication.getName()));
    }
    @PostMapping("/register")
    public ResponseEntity<List<UserModel>> registerPatient(Authentication authentication) {
        return ResponseEntity.ok(userService.getPatientUserModels(authentication.getName()));
    }


}
