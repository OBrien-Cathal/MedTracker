package com.cathalob.medtracker.payload.response;

import com.cathalob.medtracker.payload.data.PatientRegistrationData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PatientRegistrationResponse {
    private String message;
    private List<String> errors;
    private PatientRegistrationData data;
}
