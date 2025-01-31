package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.payload.data.PrescriptionDetailsData;
import com.cathalob.medtracker.payload.request.patient.AddDatedBloodPressureReadingRequest;
import com.cathalob.medtracker.payload.request.patient.AddDatedBloodPressureReadingRequestResponse;
import com.cathalob.medtracker.payload.request.patient.DatedBloodPressureDataRequest;
import com.cathalob.medtracker.payload.response.BloodPressureDataRequestResponse;
import com.cathalob.medtracker.payload.response.SubmitPrescriptionDetailsResponse;
import com.cathalob.medtracker.payload.response.TimeSeriesGraphDataResponse;
import com.cathalob.medtracker.service.impl.BloodPressureDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/blood-pressure")
@RequiredArgsConstructor
public class BloodPressureController {

    private final BloodPressureDataService bloodPressureDataService;

    @GetMapping("/systole-graph-data")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<TimeSeriesGraphDataResponse> getSystoleGraphData(Authentication authentication) {
        return ResponseEntity.ok(bloodPressureDataService.getSystoleGraphData(authentication.getName()));
    }

    @GetMapping("/systole-graph-data/patient")
    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    public ResponseEntity<TimeSeriesGraphDataResponse> getPatientSystoleGraphData(
            @RequestParam(required = false, name = "id") Long patientId,
            Authentication authentication) {
        return ResponseEntity.ok(bloodPressureDataService.getPatientSystoleGraphData(patientId, authentication.getName()));
    }


    @PostMapping("/blood-pressure-daily-data")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<BloodPressureDataRequestResponse> getBloodPressureDailyData(
            @RequestBody DatedBloodPressureDataRequest datedBloodPressureDataRequest,
            Authentication authentication) {
        return ResponseEntity.ok(bloodPressureDataService.getBloodPressureData(datedBloodPressureDataRequest, authentication.getName()));
    }

    @PostMapping("/add-blood-pressure-daily-data")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<AddDatedBloodPressureReadingRequestResponse> addBloodPressureDailyData(
            @RequestBody AddDatedBloodPressureReadingRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(bloodPressureDataService.addBloodPressureReading(request, authentication.getName()));
    }

}
