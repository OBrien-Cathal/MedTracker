package com.cathalob.medtracker.payload.request.patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientRegistrationRequest {
    private Long practitionerId;
}
