package com.cathalob.medtracker.testdata;

import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.model.tracking.Dose;

import java.time.LocalDateTime;
import java.util.List;

public class DoseBuilder {
    private Long id;
    private DailyEvaluationBuilder dailyEvaluationBuilder = new DailyEvaluationBuilder();

    private LocalDateTime doseTime = LocalDateTime.now();

    private PrescriptionScheduleEntryBuilder prescriptionScheduleEntryBuilder = new PrescriptionScheduleEntryBuilder();

    private boolean taken = false;

    public DoseBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public DoseBuilder withDailyEvaluationBuilder(DailyEvaluationBuilder dailyEvaluationBuilder) {
        this.dailyEvaluationBuilder = dailyEvaluationBuilder;
        return this;
    }

    public DoseBuilder withDoseTime(LocalDateTime doseTime) {
        this.doseTime = doseTime;
        return this;
    }

    public DoseBuilder withPrescriptionScheduleEntryBuilder(PrescriptionScheduleEntryBuilder prescriptionScheduleEntryBuilder) {
        this.prescriptionScheduleEntryBuilder = prescriptionScheduleEntryBuilder;
        return this;
    }

    public DoseBuilder withTaken(boolean taken) {
        this.taken = taken;
        return this;
    }

    public DoseBuilder() {
    }

    public DoseBuilder(DoseBuilder copy) {
        this.id = copy.id;
        this.dailyEvaluationBuilder = copy.dailyEvaluationBuilder;
        this.doseTime = copy.doseTime;
        this.prescriptionScheduleEntryBuilder = copy.prescriptionScheduleEntryBuilder;
        this.taken = copy.taken;
    }

    public static DoseBuilder aDose() {
        return new DoseBuilder();
    }

    public DoseBuilder but() {
        return new DoseBuilder(this);
    }

    public Dose build() {
        PrescriptionScheduleEntry prescriptionScheduleEntry = prescriptionScheduleEntryBuilder.build();
        DailyEvaluation dailyEvaluation = dailyEvaluationBuilder.build();
        dailyEvaluation.setUserModel(prescriptionScheduleEntry.getPrescription().getPatient());
        return new Dose(id, dailyEvaluation, doseTime, prescriptionScheduleEntry, taken);
    }


    public static List<Dose> dosesFor(List<PrescriptionScheduleEntry> pse, DailyEvaluation dailyEvaluation) {
        return pse.stream().map(prescriptionScheduleEntry -> {
            Dose dose = aDose()
                    .withId(prescriptionScheduleEntry.getId())
                    .withDoseTime(LocalDateTime.now()).build();
            dose.setPrescriptionScheduleEntry(prescriptionScheduleEntry);
            dose.setEvaluation(dailyEvaluation);
            return dose;

        }).toList();
    }
}
