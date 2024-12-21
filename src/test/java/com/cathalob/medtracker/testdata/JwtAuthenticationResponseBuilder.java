package com.cathalob.medtracker.testdata;

import com.cathalob.medtracker.payload.response.JwtAuthenticationResponse;

public class JwtAuthenticationResponseBuilder {
    private String token = "tokenString";
    private String currentUserRole = "USER";
    private String message = "success";


    public JwtAuthenticationResponseBuilder setToken(String token) {
        this.token = token;
        return this;
    }


    public JwtAuthenticationResponseBuilder withUsername(String currentUserRole) {
        this.currentUserRole = currentUserRole;
        return this;
    }

    public JwtAuthenticationResponseBuilder withMessage(String message) {
        this.message = message;
        return this;
    }

    public JwtAuthenticationResponse build() {
        return new JwtAuthenticationResponse(token, message, currentUserRole);
    }

    public static JwtAuthenticationResponseBuilder jwtAuthenticationResponse() {
        return new JwtAuthenticationResponseBuilder();

    }

}