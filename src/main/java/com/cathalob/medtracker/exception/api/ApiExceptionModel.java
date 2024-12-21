package com.cathalob.medtracker.exception.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
@AllArgsConstructor
@Data
public class ApiExceptionModel {
    private int code;
    private String exceptionMessage;
    private Throwable exceptionCause;
    private HttpStatus status;
    private ZonedDateTime zonedDateTime;
}
