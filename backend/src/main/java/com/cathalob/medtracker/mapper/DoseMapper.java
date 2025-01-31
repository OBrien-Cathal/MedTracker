package com.cathalob.medtracker.mapper;

import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.model.tracking.Dose;
import com.cathalob.medtracker.payload.data.DailyDoseData;
import com.cathalob.medtracker.payload.data.DailyMedicationDoseData;
import com.cathalob.medtracker.payload.request.patient.AddDailyDoseDataRequest;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class DoseMapper {


    public static Dose Dose(AddDailyDoseDataRequest addDailyDoseDataRequest,
                            DailyEvaluation dailyEvaluation,
                            PrescriptionScheduleEntry prescriptionScheduleEntry) {

        Dose dose = new Dose();
        dose.setDoseTime(LocalDateTime.of(addDailyDoseDataRequest.getDate(), LocalTime.now()));
        dose.setPrescriptionScheduleEntry(prescriptionScheduleEntry);
        dose.setEvaluation(dailyEvaluation);
        dose.setTaken(addDailyDoseDataRequest.getDailyDoseData().isTaken());
        return dose;
    }


    public static DailyMedicationDoseData DailyDoseData(Prescription prescription,
                                                        List<DailyDoseData> doses) {
        return DailyMedicationDoseData.builder()
                .doseMg(prescription.getDoseMg())
                .medicationName(prescription.getMedication().getName())
                .prescriptionId(prescription.getId())
                .doses(doses)
                .build();
    }

    public static DailyDoseData DoseData(PrescriptionScheduleEntry prescriptionScheduleEntry, Dose dose) {

        if (dose == null) {
            return DailyDoseData.builder()
                    .doseId(null)
                    .prescriptionScheduleEntryId(prescriptionScheduleEntry.getId())
                    .taken(true)
                    .doseTime(null)
                    .dayStage(prescriptionScheduleEntry.getDayStage().name())
                    .build();
        }
        return DailyDoseData.builder()
                .doseId(dose.getId())
                .prescriptionScheduleEntryId(prescriptionScheduleEntry.getId())
                .taken(dose.isTaken())
                .doseTime(dose.getDoseTime())
                .dayStage(prescriptionScheduleEntry.getDayStage().name())
                .build();

    }

    public Dose dose(AddDailyDoseDataRequest addDailyDoseDataRequest,
                     DailyEvaluation dailyEvaluation,
                     PrescriptionScheduleEntry prescriptionScheduleEntry) {
        return DoseMapper.Dose(addDailyDoseDataRequest, dailyEvaluation, prescriptionScheduleEntry);
    }


    public DailyMedicationDoseData dailyDoseData(Prescription prescription,
                                                 List<DailyDoseData> doses) {
        return DoseMapper.DailyDoseData(prescription, doses);
    }

    public DailyDoseData DoseData2(PrescriptionScheduleEntry prescriptionScheduleEntry, Dose dose) {
        return DoseMapper.DoseData(prescriptionScheduleEntry, dose);
    }
}
