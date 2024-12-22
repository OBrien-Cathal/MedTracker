package com.cathalob.medtracker.payload.request;

import com.cathalob.medtracker.model.enums.USERROLE;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleChangeApprovalRequest {
    private Long roleChangeRequestId;

}
