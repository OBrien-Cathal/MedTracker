package com.cathalob.medtracker.payload.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountVerificationRequest {
    @Email(message = "Invalid email")
    private String username;
}
