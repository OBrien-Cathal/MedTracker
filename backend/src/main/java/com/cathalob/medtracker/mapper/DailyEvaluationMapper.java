package com.cathalob.medtracker.mapper;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;

import java.time.LocalDate;

public class DailyEvaluationMapper {

    public static DailyEvaluation ToDailyEvaluation(LocalDate date, UserModel userModel){
        return new DailyEvaluation(date, userModel);
    }

    public  DailyEvaluation toDailyEvaluation(LocalDate date, UserModel userModel){
        return  DailyEvaluationMapper.ToDailyEvaluation(date, userModel);
    }
}
