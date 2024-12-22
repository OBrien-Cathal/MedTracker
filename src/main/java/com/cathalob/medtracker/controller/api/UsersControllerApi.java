package com.cathalob.medtracker.controller.api;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.payload.request.RoleChangeApprovalRequest;
import com.cathalob.medtracker.payload.response.GenericRequestResponse;
import com.cathalob.medtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersControllerApi {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserModel>> getUserModels() {
        return ResponseEntity.ok(userService.getUserModels());
    }

    @PostMapping("/role-request/{roleName}")
    public ResponseEntity<GenericRequestResponse> submitRoleChangeRequest(
            @PathVariable("roleName") String roleName,
            Authentication authentication) {
        GenericRequestResponse requestResponse = userService.submitRoleChange(roleName, authentication.getName());
        return ResponseEntity.ok(requestResponse);
    }

    @PostMapping("/role-request/approve")
    public ResponseEntity<GenericRequestResponse> approveRoleChange(
            @RequestBody RoleChangeApprovalRequest approvalRequest,
            Authentication authentication) {
        GenericRequestResponse requestResponse = userService.approveRoleChange(approvalRequest.getRoleChangeRequestId(),
                authentication.getName());
        return ResponseEntity.ok(requestResponse);
    }


}
