package com.cathalob.medtracker.testdata;

import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.prescription.Prescription;

import java.time.LocalDateTime;

public class PrescriptionBuilder {
    private Long id;
    private int doseMg = 10;
    private MedicationBuilder medicationBuilder = new MedicationBuilder();

    private UserModelBuilder patientUserModelBuilder = new UserModelBuilder().withRole(USERROLE.USER);
    private UserModelBuilder practitionerUserModelBuilder = new UserModelBuilder().withRole(USERROLE.PRACTITIONER);

    private LocalDateTime beginTime = LocalDateTime.now();

    private LocalDateTime endTime;

    public PrescriptionBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public PrescriptionBuilder withDoseMg(int doseMg) {
        this.doseMg = doseMg;
        return this;
    }

    public PrescriptionBuilder with(MedicationBuilder medicationBuilder) {
        this.medicationBuilder = medicationBuilder;
        return this;
    }

    public PrescriptionBuilder withPatient(UserModelBuilder patientUserModelBuilder) {
        this.patientUserModelBuilder = patientUserModelBuilder;
        return this;
    }

    public PrescriptionBuilder withPractitioner(UserModelBuilder practitionerUserModelBuilder) {
        this.practitionerUserModelBuilder = practitionerUserModelBuilder;
        return this;
    }

    public PrescriptionBuilder withBeginTime(LocalDateTime beginTime) {
        this.beginTime = beginTime;
        return this;
    }

    public PrescriptionBuilder withEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public Prescription build() {
        return new Prescription(id, doseMg, medicationBuilder.build(),
                patientUserModelBuilder.build(),
                practitionerUserModelBuilder.build(), beginTime, endTime);
    }

    public static PrescriptionBuilder aPrescription() {
        return new PrescriptionBuilder();
    }

    public PrescriptionBuilder but() {
        return new PrescriptionBuilder(this);
    }

    public PrescriptionBuilder() {
    }

    public PrescriptionBuilder(PrescriptionBuilder copy) {
        this.id = copy.id;
        this.doseMg = copy.doseMg;
        this.medicationBuilder = copy.medicationBuilder;
        this.patientUserModelBuilder = copy.patientUserModelBuilder;
        this.practitionerUserModelBuilder = copy.practitionerUserModelBuilder;
        this.beginTime = copy.beginTime;
        this.endTime = copy.endTime;
    }


}
