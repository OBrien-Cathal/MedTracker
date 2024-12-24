package com.cathalob.medtracker.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoleChangeStatusResponse {
    private boolean adminRoleChangeExists;
    private boolean approvedAdminRoleChange;
    private boolean practitionerRoleChangeExists;
    private boolean approvedPractitionerRoleChange;

}
