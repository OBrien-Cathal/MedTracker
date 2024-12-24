package com.cathalob.medtracker.payload.data;

import com.cathalob.medtracker.model.enums.USERROLE;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoleChangeData {
    private Long id;
    private String status;
    private USERROLE userRole;
}
