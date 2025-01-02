package com.cathalob.medtracker.payload.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PatientRegistrationData {
    private Long id;
    private Long practitionerId;
    private boolean approved;
}
