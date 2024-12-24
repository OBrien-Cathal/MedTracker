package com.cathalob.medtracker.payload.response;

import com.cathalob.medtracker.payload.data.RoleChangeData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoleChangeSubmissionResponse {
    private RoleChangeData roleChangeData;

}
