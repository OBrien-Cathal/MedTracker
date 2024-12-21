package com.cathalob.medtracker.testdata;

import com.cathalob.medtracker.payload.request.SignUpRequest;

public class SignUpRequestBuilder extends AuthenticationRequestBuilder {
    public SignUpRequest build() {
        return new SignUpRequest(username, password);
    }

    public static SignUpRequestBuilder aSignUpRequest() {
        return new SignUpRequestBuilder();
    }

    public SignUpRequestBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public SignUpRequestBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

}
