package com.cathalob.medtracker.controller.api;

import com.cathalob.medtracker.payload.response.GenericRequestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/medications")
@RequiredArgsConstructor
public class MedicationsController {

    @GetMapping
    public ResponseEntity<GenericRequestResponse> getMedications(
            Authentication authentication) {
        GenericRequestResponse requestResponse = new GenericRequestResponse(
                true,
                "Stub");
        return ResponseEntity.ok(requestResponse);
    }
    @PostMapping("/addMedication")
    public ResponseEntity<GenericRequestResponse> addMedication(
            Authentication authentication) {
        GenericRequestResponse requestResponse = new GenericRequestResponse(
                true,
                "Stub");
        return ResponseEntity.ok(requestResponse);
    }
}