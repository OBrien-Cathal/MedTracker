package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.exception.validation.medication.MedicationValidationException;
import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.payload.response.AddMedicationResponse;
import com.cathalob.medtracker.service.impl.MedicationsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/medications")
@RequiredArgsConstructor
public class MedicationsController {
    private final MedicationsService medicationsService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    public ResponseEntity<List<Medication>> getMedications() {
        return ResponseEntity.ok(medicationsService.getMedications());
    }

    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    @PostMapping("/add")
    public ResponseEntity<AddMedicationResponse> addMedication(@RequestBody @Valid Medication medication) {
        AddMedicationResponse response;
        try {
            response = medicationsService.addMedication(medication);
        } catch (MedicationValidationException exception) {
           response = AddMedicationResponse.Failed(List.of(exception.getMessage()));
        }
        return ResponseEntity.ok(response);
    }
}