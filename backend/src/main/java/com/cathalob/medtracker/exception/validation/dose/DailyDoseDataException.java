package com.cathalob.medtracker.exception.validation.dose;

import com.cathalob.medtracker.exception.validation.ValidatorException;
import lombok.Getter;

import java.util.List;

@Getter
public class DailyDoseDataException extends ValidatorException {

    public DailyDoseDataException(List<String> errors) {
        super(errors);
    }
}
