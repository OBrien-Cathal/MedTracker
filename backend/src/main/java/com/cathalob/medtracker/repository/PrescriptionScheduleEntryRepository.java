package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionScheduleEntryRepository extends JpaRepository<PrescriptionScheduleEntry, Long> {


    List<PrescriptionScheduleEntry> findByPrescription(Prescription prescription);
    List<PrescriptionScheduleEntry> findByPrescriptions(List<Prescription> prescription);



}
