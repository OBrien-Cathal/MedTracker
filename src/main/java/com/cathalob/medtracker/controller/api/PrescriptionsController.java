package com.cathalob.medtracker.controller.api;

import com.cathalob.medtracker.payload.response.GenericRequestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/prescriptions")
@RequiredArgsConstructor
public class PrescriptionsController {

    @GetMapping
    public ResponseEntity<GenericRequestResponse> getPrescriptions(
            Authentication authentication) {
        GenericRequestResponse requestResponse = new GenericRequestResponse(
                true,
                "Stub");
        return ResponseEntity.ok(requestResponse);
    }
    @PostMapping("/addPrescription")
    public ResponseEntity<GenericRequestResponse> addPrescription(
            Authentication authentication) {
        GenericRequestResponse requestResponse = new GenericRequestResponse(
                true,
                "Stub");
        return ResponseEntity.ok(requestResponse);
    }
}
