package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PrescriptionScheduleEntryRepository extends JpaRepository<PrescriptionScheduleEntry, Long> {


    List<PrescriptionScheduleEntry> findByPrescription(Prescription prescription);
    @Query( "select o from PRESCRIPTIONSCHEDULEENTRY o where o.prescription.id in :ids" )
    List<PrescriptionScheduleEntry> findByPrescriptionIds(List<Long> ids);



}
