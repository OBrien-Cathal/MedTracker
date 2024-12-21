package com.cathalob.medtracker.service.api;

import com.cathalob.medtracker.payload.request.SignInRequest;
import com.cathalob.medtracker.payload.request.SignUpRequest;
import com.cathalob.medtracker.payload.response.JwtAuthenticationResponse;

public interface AuthenticationServiceApi {
    JwtAuthenticationResponse signUp(SignUpRequest request);

    JwtAuthenticationResponse signIn(SignInRequest request);
}
