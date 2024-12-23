package com.cathalob.medtracker.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class GenericRequestResponse {
    private boolean requestSucceeded;
    private String message;
    @Singular
    private List<String> errors;

    public GenericRequestResponse(boolean b) {
        errors = new ArrayList<>();
        this.requestSucceeded = b;
        this.message = b ? getSucceededMessage() : getFailedMessage();
    }

    public GenericRequestResponse(boolean requestSucceeded, String message) {
        errors = new ArrayList<>();
        this.requestSucceeded = requestSucceeded;
        this.message = message;
    }

    public GenericRequestResponse success() {
        this.requestSucceeded = true;
        this.setMessage(getSucceededMessage());
        return this;
    }

    public GenericRequestResponse success(String successMessage) {
        this.requestSucceeded = true;
        this.setMessage(getSucceededMessage() + ": " + successMessage);
        return this;
    }

    public GenericRequestResponse failure() {
        this.requestSucceeded = false;
        this.setMessage(getFailedMessage());
        return this;
    }

    public GenericRequestResponse failure(List<String> validationErrors) {
        this.requestSucceeded = false;
        this.setMessage(getFailedMessage() + ": Validation");
        this.setErrors(validationErrors);
        return this;
    }


    private static String getFailedMessage() {
        return "Failed";
    }

    private static String getSucceededMessage() {
        return "Succeeded";
    }
}
