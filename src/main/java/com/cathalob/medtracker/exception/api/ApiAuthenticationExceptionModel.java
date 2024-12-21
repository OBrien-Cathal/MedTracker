package com.cathalob.medtracker.exception.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
@AllArgsConstructor
@Data
public class ApiAuthenticationExceptionModel {
    private int code;
    private HttpStatus status;
    private String message;
    private String exceptionMessage;
    private String requestURL;
    private ZonedDateTime zonedDateTime;
}
