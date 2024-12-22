package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.testdata.DailyEvaluationBuilder;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")

class BloodPressureReadingRepositoryTests {
    @Autowired
    private BloodPressureReadingRepository bloodPressureReadingRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test

    public void givenBloodPressureReading_whenSaved_thenReturnSavedBloodPressureReading() {

        DailyEvaluation dailyEvaluation = DailyEvaluationBuilder.aDailyEvaluation().build();
        testEntityManager.persist(dailyEvaluation.getUserModel());
        testEntityManager.persist(dailyEvaluation);

        BloodPressureReading bloodPressureReading = new BloodPressureReading();
        bloodPressureReading.setReadingTime(LocalDateTime.now());
        bloodPressureReading.setDiastole(80);
        bloodPressureReading.setSystole(120);
        bloodPressureReading.setHeartRate(60);
        bloodPressureReading.setDayStage(DAYSTAGE.MIDDAY);
        bloodPressureReading.setDailyEvaluation(dailyEvaluation);

//    when
        BloodPressureReading saved = bloodPressureReadingRepository.save(bloodPressureReading);

//    then
        assertThat(saved.getId()).isEqualTo(1);
    }

}