package com.cathalob.medtracker.payload.request;

import com.cathalob.medtracker.model.enums.USERROLE;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleChangeRequest {
    @Valid
    private USERROLE newRole;
}
