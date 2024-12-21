package com.cathalob.medtracker.testdata;

import com.cathalob.medtracker.payload.request.SignInRequest;

public class SignInRequestBuilder extends AuthenticationRequestBuilder {
    public SignInRequest build() {
        return new SignInRequest(username, password);
    }

    public static SignInRequestBuilder aSignInRequest() {
        return new SignInRequestBuilder();
    }
    public SignInRequestBuilder withUsername(String username) {
        this.username=username;
        return this;
    }
    public SignInRequestBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

}