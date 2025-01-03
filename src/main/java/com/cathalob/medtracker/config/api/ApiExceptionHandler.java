package com.cathalob.medtracker.config.api;

import com.cathalob.medtracker.exception.api.ApiAuthenticationExceptionModel;
import com.cathalob.medtracker.exception.api.ApiExceptionModel;
import com.cathalob.medtracker.exception.ExternalException;
import com.cathalob.medtracker.exception.InternalException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@RestControllerAdvice
public class ApiExceptionHandler {

    private final HttpServletRequest httpServletRequest;
    private final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    public ApiExceptionHandler(HttpServletRequest httpServletRequest) {

        this.httpServletRequest = httpServletRequest;

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> getServerExceptionHandler(@NotNull Exception exception) {

        if (exception instanceof ExpiredJwtException || exception instanceof AuthorizationDeniedException) {

            ApiAuthenticationExceptionModel apiAuthenticationExceptionModel = new ApiAuthenticationExceptionModel(
                    HttpStatus.UNAUTHORIZED.value(),
                    HttpStatus.UNAUTHORIZED,
                    "Authentication failed",
                    exception.getMessage(),
                    httpServletRequest.getRequestURL().toString(),
                    ZonedDateTime.now(ZoneId.of("Z"))
            );

            return new ResponseEntity<>(apiAuthenticationExceptionModel, HttpStatus.UNAUTHORIZED);

        }

        if (exception instanceof InternalException) {
            logger.error("Internal: ", exception);
            ApiExceptionModel apiExceptionModel = new ApiExceptionModel(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ExternalException.getErrorMessageWithCode((InternalException) exception),
                    exception.getCause(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ZonedDateTime.now(ZoneId.of("Z"))
            );

            return new ResponseEntity<>(apiExceptionModel, HttpStatus.INTERNAL_SERVER_ERROR);

        }

        logger.error("Something went wrong which is causing the application to fail: ", exception);

        ApiExceptionModel apiExceptionModel = new ApiExceptionModel(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                exception.getMessage(),
                "Server Private Exception",
                exception.getCause(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(apiExceptionModel, HttpStatus.INTERNAL_SERVER_ERROR);

    }

}