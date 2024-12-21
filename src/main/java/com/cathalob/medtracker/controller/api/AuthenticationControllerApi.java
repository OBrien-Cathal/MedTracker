package com.cathalob.medtracker.controller.api;

import com.cathalob.medtracker.payload.request.AccountVerificationRequest;
import com.cathalob.medtracker.payload.request.AuthenticationVerificationRequest;
import com.cathalob.medtracker.payload.request.SignInRequest;
import com.cathalob.medtracker.payload.request.SignUpRequest;
import com.cathalob.medtracker.payload.response.AccountVerificationResponse;
import com.cathalob.medtracker.payload.response.AuthenticationVerificationResponse;
import com.cathalob.medtracker.payload.response.JwtAuthenticationResponse;
import com.cathalob.medtracker.service.api.impl.AuthenticationServiceApi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationControllerApi {
    private final AuthenticationServiceApi authenticationService;
    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponse> signUp(@RequestBody @Valid SignUpRequest request){
        return ResponseEntity.ok(authenticationService.signUp(request));
    }
    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> signUp(@RequestBody @Valid SignInRequest request){
        return ResponseEntity.ok(authenticationService.signIn(request));
    }
    @PostMapping("/verify")
    public ResponseEntity<AuthenticationVerificationResponse> verify(@RequestBody AuthenticationVerificationRequest request){
        return ResponseEntity.ok(authenticationService.verifyAuthentication(request));
    }
    @PostMapping("/checkaccount")
    public ResponseEntity<AccountVerificationResponse> checkAccountExists(@RequestBody @Valid AccountVerificationRequest request){
        return ResponseEntity.ok(authenticationService.checkAccountExists(request));
    }
}



