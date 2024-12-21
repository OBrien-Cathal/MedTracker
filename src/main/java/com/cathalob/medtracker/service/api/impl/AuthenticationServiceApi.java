package com.cathalob.medtracker.service.api.impl;

import com.cathalob.medtracker.payload.request.AccountVerificationRequest;
import com.cathalob.medtracker.payload.request.AuthenticationVerificationRequest;
import com.cathalob.medtracker.payload.request.SignInRequest;
import com.cathalob.medtracker.payload.request.SignUpRequest;
import com.cathalob.medtracker.payload.response.AccountVerificationResponse;
import com.cathalob.medtracker.payload.response.AuthenticationVerificationResponse;
import com.cathalob.medtracker.payload.response.JwtAuthenticationResponse;
import com.cathalob.medtracker.exception.UserAlreadyExistsException;
import com.cathalob.medtracker.exception.UserNotFound;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.repository.UserModelRepository;
import com.cathalob.medtracker.service.api.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceApi implements com.cathalob.medtracker.service.api.AuthenticationServiceApi {
    private final UserModelRepository userModelRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        if (userModelRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        var user = UserModel.builder().username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(USERROLE.USER).build();
        userModelRepository.save(user);
        UserDetails userDetails = getUserDetails(user);
        var jwt = jwtService.generateToken(userDetails);
        return JwtAuthenticationResponse.builder()
                .token(jwt)
                .message("success")
                .currentUserRole(user.getRole().name())
                .build();
    }

    @Override
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        String username = request.getUsername();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.getPassword()));
        var user = userModelRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound(username));
        UserDetails userDetails = getUserDetails(user);
        var jwt = jwtService.generateToken(userDetails);
        return JwtAuthenticationResponse.builder()
                .token(jwt)
                .message("success")
                .currentUserRole(user.getRole().name())
                .build();
    }

    private static UserDetails getUserDetails(UserModel user) {
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(String.valueOf(user.getRole()))
                .build();
    }

    private static UserDetails getUserDetails(String username) {
        return User.builder()
                .username(username).build();
    }

    public AuthenticationVerificationResponse verifyAuthentication(AuthenticationVerificationRequest request) {
        String token = request.getToken();
        boolean tokenValid = false;
        if (token.isEmpty()) return getAuthenticationVerificationResponse(tokenValid);
        try {
            String username = jwtService.extractUserName(token);
            Optional<UserModel> maybeUserModel = userModelRepository.findByUsername(username);
            if (maybeUserModel.isEmpty()) {
                return getAuthenticationVerificationResponse(tokenValid);
            }
            tokenValid = jwtService.isTokenValid(token,
                    getUserDetails(maybeUserModel.get()));

        } catch (ExpiredJwtException expiredJwtException) {
            return getAuthenticationVerificationResponse(tokenValid);
        }
        return getAuthenticationVerificationResponse(tokenValid);
    }

    private static AuthenticationVerificationResponse getAuthenticationVerificationResponse(boolean tokenValid) {
        return AuthenticationVerificationResponse.builder().authenticated(tokenValid).build();
    }

    public AccountVerificationResponse checkAccountExists(AccountVerificationRequest request) {
        boolean userExists = userModelRepository.findByUsername(request.getUsername()).isPresent();
        System.out.println("Account " + ((userExists) ? "EXISTS" : "NOT EXISTS") + " for User: " + request.getUsername());
        return AccountVerificationResponse.builder().accountExists(userExists).build();
    }
}
