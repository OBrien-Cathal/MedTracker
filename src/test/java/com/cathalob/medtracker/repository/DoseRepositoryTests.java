package com.cathalob.medtracker.repository;


import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import com.cathalob.medtracker.model.tracking.Dose;

import static com.cathalob.medtracker.testdata.DoseBuilder.aDose;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;


@DataJpaTest
@ActiveProfiles("test")

class DoseRepositoryTests {
    @Autowired
    private DoseRepository doseRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void givenDose_whenSaved_thenReturnSavedDose() {
//          given
        Dose dose = aDose().build();
        persistPrerequisites(dose);

//      when
        Dose saved = doseRepository.save(dose);

//      then
        assertThat(saved.getId()).isGreaterThan(1L);
    }

    @Test
    public void givenDose_whenSavedAndQueried_thenReturnOnlyDosesForUserModelId() {
//          given
        Dose dose = aDose().build();
        Dose otherDose = aDose().build();

        persistPrerequisites(dose);
        persistPrerequisites(otherDose);
        testEntityManager.persist(otherDose);

//      when
        doseRepository.save(dose);
        List<Dose> dosesForUserId = doseRepository.findDosesForUserId(dose.getPrescriptionScheduleEntry().getPrescription().getPatient().getId());

//      then
        assertThat(doseRepository.findAll().size()).isEqualTo(2);
        assertThat(dosesForUserId.size()).isEqualTo(1);
        assertThat(dosesForUserId).allMatch(retrievedDose -> retrievedDose.getEvaluation().getUserModel().getId() > (0));

    }

    private void persistPrerequisites(Dose dose) {
        PrescriptionScheduleEntry prescriptionScheduleEntry = dose.getPrescriptionScheduleEntry();
        Prescription prescription = prescriptionScheduleEntry.getPrescription();

        testEntityManager.persist(prescription.getPatient());
        testEntityManager.persist(prescription.getPractitioner());
        testEntityManager.persist(dose.getEvaluation());
        testEntityManager.persist(prescription.getMedication());
        testEntityManager.persist(prescription);
        testEntityManager.persist(prescriptionScheduleEntry);
    }
}