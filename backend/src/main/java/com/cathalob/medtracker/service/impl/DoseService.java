package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.exception.validation.dose.DailyDoseDataException;
import com.cathalob.medtracker.mapper.DailyEvaluationMapper;
import com.cathalob.medtracker.mapper.DoseMapper;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.DAYSTAGE;

import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.model.tracking.Dose;
import com.cathalob.medtracker.payload.data.GraphData;
import com.cathalob.medtracker.payload.request.patient.AddDailyDoseDataRequest;
import com.cathalob.medtracker.payload.request.patient.GetDailyDoseDataRequest;
import com.cathalob.medtracker.payload.response.AddDailyDoseDataRequestResponse;
import com.cathalob.medtracker.payload.response.GetDailyDoseDataRequestResponse;
import com.cathalob.medtracker.payload.response.TimeSeriesGraphDataResponse;
import com.cathalob.medtracker.repository.DailyEvaluationRepository;
import com.cathalob.medtracker.repository.DoseRepository;
import com.cathalob.medtracker.repository.PatientRegistrationRepository;
import com.cathalob.medtracker.repository.PrescriptionScheduleEntryRepository;
import com.cathalob.medtracker.service.UserService;
import com.cathalob.medtracker.validate.model.DoseValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class DoseService {
    private final PrescriptionsService prescriptionsService;
    private final DoseRepository doseRepository;
    private final UserService userService;
    private final PatientRegistrationRepository patientRegistrationRepository;
    private final DailyEvaluationRepository dailyEvaluationRepository;
    private final PrescriptionScheduleEntryRepository prescriptionScheduleEntryRepository;
    private final DoseMapper doseMapper;
    private final DailyEvaluationMapper dailyEvaluationMapper;


    public List<Dose> getDoses(UserModel userModel) {
        return doseRepository.findDosesForUserId(userModel.getId());
    }


    public void saveDoses(List<Dose> newDoses) {
        doseRepository.saveAll(newDoses);
    }

    public Map<Long, Dose> getDosesById() {
        return doseRepository.findAll().stream().collect(Collectors.toMap(Dose::getId, Function.identity()));
    }

    public TimeSeriesGraphDataResponse getDoseGraphData(String patientUsername) {
        return getDoseGraphDataResponse(userService.findByLogin(patientUsername));
    }

    public TimeSeriesGraphDataResponse getPatientDoseGraphData(Long patientUserModelId, String practitionerUsername) {
        UserModel practitioner = userService.findByLogin(practitionerUsername);
        Optional<UserModel> maybePatient = userService.findUserModelById(patientUserModelId);
        if (maybePatient.isEmpty()) return TimeSeriesGraphDataResponse.Failure();
        UserModel patient = maybePatient.get();
//        validate that the practitioner is a doc of the patient, and allowed to see the patient data
        if (patientRegistrationRepository.findByUserModelAndPractitionerUserModel(patient, practitioner).isEmpty()) {
            return TimeSeriesGraphDataResponse.Failure(List.of("Only registered practitioners can view this patients data"));

        }
        return getDoseGraphDataResponse(patient);
    }

    public GetDailyDoseDataRequestResponse getDailyDoseData(GetDailyDoseDataRequest request, String username) {
        UserModel patient = userService.findByLogin(username);

        DailyEvaluation dailyEvaluationPlaceholder = dailyEvaluationMapper.toDailyEvaluation(request.getDate(), patient);
        DailyEvaluation dailyEvaluation = dailyEvaluationRepository.findById(dailyEvaluationPlaceholder.getDailyEvaluationIdClass())
                .orElse(dailyEvaluationRepository.save(dailyEvaluationPlaceholder));

        List<Prescription> prescriptionsValidOnRequestDate = prescriptionsService.getPrescriptionsValidOnDate(patient, request.getDate());

        List<PrescriptionScheduleEntry> prescriptionScheduleEntries = prescriptionScheduleEntryRepository
                .findByPrescriptionIds(prescriptionsValidOnRequestDate.stream().map(Prescription::getId).toList());

        return GetDailyDoseDataRequestResponse.Success(LocalDate.now(),
                doseMapper.dailyMedicationDoseDataList(prescriptionScheduleEntries, doseRepository.findByEvaluation(dailyEvaluation)));
    }

    public AddDailyDoseDataRequestResponse addDailyDoseData(AddDailyDoseDataRequest request, String username) throws DailyDoseDataException {
        UserModel patient = userService.findByLogin(username);

        DailyEvaluation dailyEvaluationPlaceholder = dailyEvaluationMapper.toDailyEvaluation(request.getDate(), patient);
        DailyEvaluation dailyEvaluation = dailyEvaluationRepository.findById(dailyEvaluationPlaceholder.getDailyEvaluationIdClass())
                .orElse(dailyEvaluationRepository.save(dailyEvaluationPlaceholder));

        PrescriptionScheduleEntry prescriptionScheduleEntry = prescriptionScheduleEntryRepository.findById(request.getDailyDoseData().getPrescriptionScheduleEntryId()).orElse(null);

        List<Dose> byId = doseRepository.findByPrescriptionScheduleEntryAndEvaluation(prescriptionScheduleEntry, dailyEvaluation);

        Dose addOrUpdateDose = doseMapper.dose(request, dailyEvaluation, prescriptionScheduleEntry);

        if (byId.isEmpty()) {
            DoseValidator.AddDoseValidator(addOrUpdateDose).validate();
        } else {
            Dose foundDose = byId.get(0);
            addOrUpdateDose.setId(foundDose.getId());
            DoseValidator.UpdateDoseValidator(addOrUpdateDose, foundDose).validate();
        }
        Dose saved = doseRepository.save(addOrUpdateDose);

        return AddDailyDoseDataRequestResponse.Success(LocalDate.now(), saved.getId());
    }

    private TimeSeriesGraphDataResponse getDoseGraphDataResponse(UserModel patient) {
        return TimeSeriesGraphDataResponse.Success(
                new GraphData(
                        getDoseGraphColumnNames(patient),
                        getDoseGraphData(patient)));

    }

    private List<List<Object>> getDoseGraphData(UserModel userModel) {
        List<List<Object>> listData = new ArrayList<>();
        List<Dose> doses = getDoses(userModel);

        TreeMap<LocalDate, List<Dose>> byDate = doses.stream()
                .sorted(Comparator.comparing(dose -> dose.getDoseTime().toLocalDate()))
                .collect(Collectors.groupingBy(dose -> dose.getDoseTime().toLocalDate(), TreeMap::new, Collectors.toList()));

        List<Medication> medicationList = prescriptionsService.getPatientMedications(userModel).stream().sorted(Comparator.comparing(Medication::getName)).toList();
        List<DAYSTAGE> daystageList = prescriptionsService.getPatientPrescriptionDayStages(userModel).stream().sorted(Comparator.comparing(DAYSTAGE::ordinal)).toList();
        HashMap<Medication, HashSet<LocalDate>> patientPrescriptionDatesByMedication = prescriptionsService.getPatientPrescriptionDatesByMedication(userModel);

        Optional<LocalDate> start = byDate.keySet().stream().distinct().min(LocalDate::compareTo);

        Optional<LocalDate> endDose = byDate.keySet().stream().distinct().max(LocalDate::compareTo);
        LocalDate now = LocalDate.now().plusDays(1);
        LocalDate end = endDose.isPresent() && endDose.get().isAfter(now) ? endDose.get() : now;

        List<LocalDate> daysRange = new ArrayList<>();
        if (start.isPresent()) {
            long numDays = ChronoUnit.DAYS.between(start.get(), end);
            daysRange.addAll(Stream.iterate(start.get(), date -> date.plusDays(1)).limit(numDays).toList());
        }

        for (LocalDate date : daysRange) {
            List<Dose> dosesByDate = (byDate.get(date) == null) ? new ArrayList<>() : byDate.get(date);

            List<Object> dayDoseData = new ArrayList<>();
            Map<Medication, List<Dose>> byMedication = dosesByDate.stream()
                    .collect(Collectors.groupingBy(dose -> dose.getPrescriptionScheduleEntry().getPrescription().getMedication()));

            dayDoseData.add(date);
            for (Medication medication : medicationList) {
                for (DAYSTAGE daystage : daystageList) {
                    boolean isPrescribedOnDate = patientPrescriptionDatesByMedication.containsKey(medication) && patientPrescriptionDatesByMedication.get(medication).contains(date);

                    if (byMedication.containsKey(medication)) {
                        Map<DAYSTAGE, List<Dose>> byDayStage = byMedication.get(medication).stream()
                                .collect(Collectors.groupingBy(dose -> dose.getPrescriptionScheduleEntry().getDayStage()));
                        List<Dose> doseEntriesForDayStage = byDayStage
                                .get(daystage);
                        if (byDayStage.containsKey(daystage)) {
                            if (doseEntriesForDayStage.stream().filter(Dose::isTaken).toList().isEmpty()) {
                                dayDoseData.add(0);
                            } else {
                                dayDoseData.add(doseEntriesForDayStage.stream().filter(Dose::isTaken).mapToInt(value -> value.getPrescriptionScheduleEntry().getPrescription().getDoseMg()).sum());
                            }
                        } else {
                            dayDoseData.add(isPrescribedOnDate ? 0 : null);
                        }
                    } else {
                        dayDoseData.add(isPrescribedOnDate ? 0 : null);
                    }
                }
            }
            listData.add(dayDoseData);
        }
        return listData;
    }


    private List<String> getDoseGraphColumnNames(UserModel userModel) {
        List<String> names = new ArrayList<>();
        names.add("Date");
        List<String> dayStageNames = this.prettifiedDayStageNames(prescriptionsService.getPatientPrescriptionDayStages(userModel));

        for (String medication : prescriptionsService.getPatientMedications(userModel)
                .stream()
                .map(Medication::getName)
                .sorted()
                .toList()) {
            for (String dayStage : dayStageNames) {
                names.add(medication + " (" + dayStage + ')');
            }
        }
        return names;
    }


    private List<String> prettifiedDayStageNames(List<DAYSTAGE> dayStages) {
        return dayStages.stream().map(ds -> (
                ds.toString().charAt(0) + ds.toString().substring(1).toLowerCase())).toList();
    }
}
