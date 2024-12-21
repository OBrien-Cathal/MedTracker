package com.cathalob.medtracker.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenericRequestResponse {
    private boolean requestSucceeded;
    private String message;


}
