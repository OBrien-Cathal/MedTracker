package com.cathalob.medtracker.controller.api;

import com.cathalob.medtracker.payload.response.GenericRequestResponse;
import com.cathalob.medtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserControllerApi {
    private final UserService userService;

    @PostMapping("/role-request/{roleName}")
    public ResponseEntity<GenericRequestResponse> submitRoleChangeRequest(
            @PathVariable("roleName") String roleName,
            Authentication authentication) {
        GenericRequestResponse requestResponse = userService.submitRoleChange(roleName, authentication.getName());
        return ResponseEntity.ok(requestResponse);
    }

}
