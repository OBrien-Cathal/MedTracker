package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static com.cathalob.medtracker.testdata.DailyEvaluationBuilder.aDailyEvaluation;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;


@DataJpaTest
@ActiveProfiles("test")
class DailyEvaluationRepositoryTests {
    @Autowired
    private DailyEvaluationRepository dailyEvaluationRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void givenDailyEvaluation_whenSaved_thenGetSavedDailyEvaluation() {
//        given
        DailyEvaluation dailyEvaluation = aDailyEvaluation().build();
        testEntityManager.persist(dailyEvaluation.getUserModel());

//        when
        dailyEvaluationRepository.save(dailyEvaluation);

//        when
        boolean present = dailyEvaluationRepository.findAll().stream()
                .anyMatch(d -> d.getDailyEvaluationIdClass().equals(dailyEvaluation.getDailyEvaluationIdClass()));
        assertThat(present).isTrue();
    }

    @Test
    public void givenDailyEvaluation_whenSavedAndQueried_thenReturnOnlyEvaluationsForUserId() {
//        given
        DailyEvaluation dailyEvaluation = aDailyEvaluation().build();
        DailyEvaluation otherDailyEvaluation = aDailyEvaluation().build();
        testEntityManager.persist(dailyEvaluation.getUserModel());
        testEntityManager.persist(otherDailyEvaluation.getUserModel());
        testEntityManager.persist(otherDailyEvaluation);

//        when
        dailyEvaluationRepository.save(dailyEvaluation);
        List<DailyEvaluation> dailyEvaluationsForUserModelId = dailyEvaluationRepository.findDailyEvaluationsForUserModelId(dailyEvaluation.getUserModel().getId());

//        when
        assertThat(dailyEvaluationRepository.findAll().size()).isEqualTo(2);
        assertThat(dailyEvaluationsForUserModelId.size()).isEqualTo(1);
        assertThat(dailyEvaluationsForUserModelId).allMatch(de -> de.getUserModel().getId() >(0L));

    }

}