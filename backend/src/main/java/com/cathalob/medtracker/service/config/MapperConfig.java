package com.cathalob.medtracker.service.config;

import com.cathalob.medtracker.mapper.DailyEvaluationMapper;
import com.cathalob.medtracker.mapper.DoseMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public DailyEvaluationMapper dailyEvaluationMapper() {
        return new DailyEvaluationMapper();
    }


    @Bean
    public DoseMapper doseMapper() {
        return new DoseMapper();
    }

}

