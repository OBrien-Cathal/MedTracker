package com.cathalob.medtracker.validate.model.errors;

import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.validate.ValidationError;



public class UserModelError extends ValidationError {

    public static String UserNotExists() {
        return "User does not exist";
    }

    public static String UserNotExists(USERROLE expectedRole) {
        return expectedRole.name() + " User does not exist";
    }
}


