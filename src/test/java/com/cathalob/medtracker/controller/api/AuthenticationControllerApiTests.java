package com.cathalob.medtracker.controller.api;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.dao.request.AccountVerificationRequest;
import com.cathalob.medtracker.dao.request.AuthenticationVerificationRequest;
import com.cathalob.medtracker.dao.request.SignInRequest;
import com.cathalob.medtracker.dao.request.SignUpRequest;
import com.cathalob.medtracker.dao.response.AccountVerificationResponse;
import com.cathalob.medtracker.dao.response.AuthenticationVerificationResponse;
import com.cathalob.medtracker.dao.response.JwtAuthenticationResponse;
import com.cathalob.medtracker.err.UserAlreadyExistsException;
import com.cathalob.medtracker.err.UserNotFound;
import com.cathalob.medtracker.service.api.impl.AuthenticationServiceApi;
import com.cathalob.medtracker.service.api.impl.JwtServiceImpl;
import com.cathalob.medtracker.service.impl.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = AuthenticationControllerApi.class)
class AuthenticationControllerApiTests {


    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtServiceImpl jwtService;
    @MockBean
    private AuthenticationServiceApi authenticationServiceApi;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void givenSignUpRequest_whenSignUp_thenReturnOk() throws Exception {
        //given - precondition or setup
        SignUpRequest signUpRequest = SignUpRequest.builder().username("name").password("abc").build();
        given(authenticationServiceApi.signUp(any(SignUpRequest.class)))
                .willReturn(JwtAuthenticationResponse.builder()
                        .token("abc")
                        .message("success")
                        .build());

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)));

        // then - verify the output
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("abc")))
                .andExpect(jsonPath("$.message", is("success")));
    }

    @Test
    public void givenSignUpRequestForExistingUsername_whenSignUp_thenThrowUserAlreadyExists() throws Exception {
        //given - precondition or setup
        SignUpRequest signUpRequest = SignUpRequest.builder().username("name").password("abc").build();
        given(authenticationServiceApi.signUp(any(SignUpRequest.class)))
                .willThrow(new UserAlreadyExistsException(signUpRequest.getUsername()));

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)));
        // then - verify the output
        response.andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errors",
                        is(Collections.singletonList(UserAlreadyExistsException.expandedMessage(signUpRequest.getUsername())))));
    }

    @Test
    public void givenSignInRequest_whenSignIn_thenReturnOk() throws Exception {
        //given - precondition or setup
        SignInRequest signInRequest = SignInRequest.builder().username("name").password("abc").build();
        given(authenticationServiceApi.signIn(any(SignInRequest.class)))
                .willReturn(JwtAuthenticationResponse.builder()
                        .token("abc")
                        .message("success")
                        .build());

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/api/v1/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInRequest)));

        // then - verify the output
        response
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("abc")))
                .andExpect(jsonPath("$.message", is("success")));
    }

    @Test
    public void givenSignInRequestForNonExistentUsername_whenSignIn_thenThrowUserNotFound() throws Exception {
        //given - precondition or setup
        SignInRequest signInRequest = SignInRequest.builder().username("name").password("abc").build();
        given(authenticationServiceApi.signIn(any(SignInRequest.class)))
                .willThrow(new UserNotFound(signInRequest.getUsername()));

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/api/v1/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInRequest)));

        // then - verify the output
        response
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errors",
                        is(Collections.singletonList(UserNotFound.expandedMessage(signInRequest.getUsername())))));
    }
    @Test
    public void givenAuthenticationVerificationRequest_whenVerify_thenReturnAuthenticationVerificationResponseWithAuthenticatedTrue()
            throws Exception {
        //given - precondition or setup
        verifyAuthenticated(true);
    }
    @Test
    public void givenExpiredAuthenticationVerificationRequest_whenVerify_thenReturnAuthenticationVerificationResponseWithAuthenticatedFalse()
            throws Exception {
        //given - precondition or setup
        verifyAuthenticated(false);
    }

    private void verifyAuthenticated(boolean authenticated) throws Exception {
//        given
        AuthenticationVerificationRequest authenticationVerificationRequest =
                AuthenticationVerificationRequest.builder().token("aTokenString").build();
        given(authenticationServiceApi.verifyAuthentication(any(AuthenticationVerificationRequest.class)))
                .willReturn(AuthenticationVerificationResponse.builder().authenticated(authenticated).build());

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/api/v1/auth/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationVerificationRequest)));

        // then - verify the output
        response
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated",
                        is(authenticated)));
    }

    @Test
    public void givenAccountVerificationRequest_whenVerify_thenReturnAccountVerificationResponseWithAccountExistsTrue()
            throws Exception {
        //given - precondition or setup
        checkAccountExists(true);
    }
    @Test
    public void givenAccountVerificationRequest_whenVerify_thenReturnAccountVerificationResponseWithAccountExistsFalse()
            throws Exception {
        //given - precondition or setup
        checkAccountExists(false);
    }

    private void checkAccountExists(boolean accountExists) throws Exception {
        //given
        AccountVerificationRequest accountVerificationRequest = AccountVerificationRequest.builder().username("user@user.com").build();
        given(authenticationServiceApi.checkAccountExists(any(AccountVerificationRequest.class)))
                .willReturn(AccountVerificationResponse.builder().accountExists(accountExists).build());

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/api/v1/auth/checkaccount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountVerificationRequest)));

        // then - verify the output
        response
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountExists",
                        is(accountExists)));
    }

}
