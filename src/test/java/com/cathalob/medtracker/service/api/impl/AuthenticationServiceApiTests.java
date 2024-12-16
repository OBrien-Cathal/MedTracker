package com.cathalob.medtracker.service.api.impl;

import com.cathalob.medtracker.dao.request.SignInRequest;
import com.cathalob.medtracker.dao.request.SignUpRequest;
import com.cathalob.medtracker.dao.response.JwtAuthenticationResponse;
import com.cathalob.medtracker.err.UserAlreadyExistsException;
import com.cathalob.medtracker.err.UserNotFound;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.repository.UserModelRepository;
import com.cathalob.medtracker.testdata.UserModelBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceApiTests {
    @Mock
    private UserModelRepository userModelRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtServiceImpl jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private AuthenticationServiceApi authenticationServiceApi;
    @DisplayName("Sign Up request returns jwtAuthenticationResponse")
    @Test
    public void givenSignupRequest_whenSignup_thenReturnJwtAuthenticationResponse() {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aUserModel().build();
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username(userModel.getUsername())
                .password(userModel.getPassword())
                .build();
        given(userModelRepository.findByUsername(signUpRequest.getUsername()))
                .willReturn(Optional.empty());
        given(passwordEncoder.encode(userModel.getPassword())).willReturn(userModel.getPassword());
//        given(userModelRepository.save(userModel)).willReturn(userModel);
        String tokenString = "tokenString";
        given(jwtService.generateToken(getUserDetails(userModel))).willReturn(tokenString);

        // when - action or the behaviour that we are going test
        JwtAuthenticationResponse jwtAuthenticationResponse = authenticationServiceApi.signUp(signUpRequest);

        // then - verify the output
        verify(userModelRepository, times(1)).save(any(UserModel.class));
        assertThat(jwtAuthenticationResponse).isNotNull();
        assertThat(jwtAuthenticationResponse.getToken()).isEqualTo(tokenString);
        assertThat(jwtAuthenticationResponse.getMessage()).isEqualTo("success");
    }
    @DisplayName("Sign Up existent user throws UserAlreadyExists exception")
    @Test
    public void givenSignUpRequestForExistingUser_whenSignUp_thenThrowUserAlreadyExists() {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aUserModel().build();
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username(userModel.getUsername())
                .password(userModel.getPassword())
                .build();
        given(userModelRepository.findByUsername(signUpRequest.getUsername()))
                .willReturn(Optional.of(userModel));

        // when - action or the behaviour that we are going test
        assertThrows(UserAlreadyExistsException.class, () -> authenticationServiceApi.signUp(signUpRequest));

        // then - verify the output
        verify(userModelRepository, never()).save(any(UserModel.class));
    }
    @DisplayName("Sign In request returns jwtAuthenticationResponse")
    @Test
    public void givenSignInRequest_whenSignIn_thenReturnJwtAuthenticationResponse() {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aUserModel().build();
        SignInRequest signInRequest = SignInRequest.builder()
                .username(userModel.getUsername())
                .password(userModel.getPassword())
                .build();
        given(userModelRepository.findByUsername(signInRequest.getUsername()))
                .willReturn(Optional.of(userModel));
        String tokenString = "tokenString";
        given(jwtService.generateToken(getUserDetails(userModel))).willReturn(tokenString);

        // when - action or the behaviour that we are going test
        JwtAuthenticationResponse jwtAuthenticationResponse = authenticationServiceApi.signIn(signInRequest);

        // then - verify the output
        assertThat(jwtAuthenticationResponse).isNotNull();
        assertThat(jwtAuthenticationResponse.getToken()).isEqualTo(tokenString);
        assertThat(jwtAuthenticationResponse.getMessage()).isEqualTo("success");
    }
    @DisplayName("Sign In non existent user throws UserNotFound exception")
    @Test
    public void givenSignInRequestForNonExistingUser_whenSignIn_thenThrowUserNotFound() {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aUserModel().build();
        SignInRequest signInRequest = SignInRequest.builder()
                .username(userModel.getUsername())
                .password(userModel.getPassword())
                .build();
        given(userModelRepository.findByUsername(signInRequest.getUsername()))
                .willReturn(Optional.empty());

        // when - action or the behaviour that we are going test
        assertThrows(UserNotFound.class, () -> authenticationServiceApi.signIn(signInRequest));

        // then - verify the output
        verify(jwtService, never()).generateToken(any(UserDetails.class));
    }
    private static UserDetails getUserDetails(UserModel user) {
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(String.valueOf(user.getRole()))
                .build();
    }
}