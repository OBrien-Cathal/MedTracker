package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.exception.validation.dose.DailyDoseDataException;
import com.cathalob.medtracker.mapper.DailyEvaluationMapper;
import com.cathalob.medtracker.mapper.DoseMapper;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.model.tracking.Dose;
import com.cathalob.medtracker.payload.data.DailyDoseData;
import com.cathalob.medtracker.payload.request.patient.AddDailyDoseDataRequest;
import com.cathalob.medtracker.payload.request.patient.GetDailyDoseDataRequest;
import com.cathalob.medtracker.payload.response.AddDailyDoseDataRequestResponse;
import com.cathalob.medtracker.payload.response.GetDailyDoseDataRequestResponse;
import com.cathalob.medtracker.repository.DailyEvaluationRepository;
import com.cathalob.medtracker.repository.DoseRepository;
import com.cathalob.medtracker.repository.PrescriptionScheduleEntryRepository;
import com.cathalob.medtracker.service.UserService;
import com.cathalob.medtracker.testdata.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.cathalob.medtracker.testdata.DailyEvaluationBuilder.aDailyEvaluation;
import static com.cathalob.medtracker.testdata.DoseBuilder.aDose;
import static com.cathalob.medtracker.testdata.PrescriptionBuilder.aPrescription;
import static com.cathalob.medtracker.testdata.PrescriptionScheduleEntryBuilder.aPrescriptionScheduleEntry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
class DoseServiceTests {
    @InjectMocks
    private DoseService doseService;
    @Mock
    private DoseRepository doseRepository;
    @Mock
    private UserService userService;
    @Mock
    private DailyEvaluationRepository dailyEvaluationRepository;

    @Mock
    private PrescriptionsService prescriptionsService;

    @Mock
    private PrescriptionScheduleEntryRepository prescriptionScheduleEntryRepository;
    @Mock
    private DoseMapper doseMapper;


    @Mock
    private DailyEvaluationMapper dailyEvaluationMapper;


    private List<Dose> buildDosesList(List<String> medicationNames, List<DAYSTAGE> daystageList) {
        UserModelBuilder userModelBuilder = UserModelBuilder.aUserModel().withRole(USERROLE.PATIENT);

        DailyEvaluationBuilder dailyEvaluationBuilder = aDailyEvaluation()
                .with(userModelBuilder);

        List<PrescriptionBuilder> prescriptionBuilders = Stream.iterate(0, n -> n + 1)
                .limit(medicationNames.size())
                .map(medIndex -> {
                            Long id = (long) (medIndex + 1);
                            return aPrescription()
                                    .withPatient(userModelBuilder)
                                    .withId(id)
                                    .with(MedicationBuilder.aMedication()
                                            .withId(id)
                                            .withName(medicationNames.get(medIndex)))
                                    .withEndTime(LocalDateTime.now().plusDays(5));
                        }
                ).toList();

        return Stream.iterate(0, p -> p + 1)
                .limit(prescriptionBuilders.size()).flatMap(
                        pIndex -> Stream.iterate(0, n -> n + 1)
                                .limit(daystageList.size())
                                .map(index -> {
                                    long id = ((long) daystageList.size() * pIndex) + (index + 1);

                                    PrescriptionScheduleEntryBuilder prescriptionScheduleEntryBuilder = aPrescriptionScheduleEntry().withId(id)
                                            .with(prescriptionBuilders.get(pIndex));
                                    return aDose()
                                            .withId(id)
                                            .withDailyEvaluationBuilder(dailyEvaluationBuilder)
                                            .withPrescriptionScheduleEntryBuilder(prescriptionScheduleEntryBuilder)
                                            .build();

                                })).toList();

    }


    @DisplayName("Test DoseData creation when full daily data ALREADY exists ")
    @Test
    public void givenGetDailyDoseDataRequestResponse_whenGetDailyDoseDataForDayWithCompleteDoseData_thenReturnGetDailyDoseDataResponseWithDoseData() {
        //given - precondition or setup
        LocalDate requestDate = LocalDate.now();
        GetDailyDoseDataRequest request = GetDailyDoseDataRequest.builder().date(requestDate).build();

        UserModelBuilder patientBuilder = UserModelBuilder.aUserModel().withRole(USERROLE.PATIENT);
        UserModel patient = patientBuilder.build();

        MedicationBuilder med1 = MedicationBuilder.aMedication().withId(1L).withName("Med1");
        MedicationBuilder med2 = MedicationBuilder.aMedication().withId(2L).withName("Med2");

        Prescription prescription1 = aPrescription().withId(1L)
                .with(med1)
                .withBeginTime(LocalDateTime.of(requestDate.plusDays(-1), LocalTime.now()))
                .build();
        Prescription prescription2 = aPrescription().withId(2L)
                .with(med2)
                .withBeginTime(LocalDateTime.of(requestDate.plusDays(-1), LocalTime.now()))
                .build();

        prescription1.setPatient(patient);
        prescription2.setPatient(patient);

        PrescriptionScheduleEntry entryP1e1 = aPrescriptionScheduleEntry().withDayStage(DAYSTAGE.WAKEUP).withId(1L).build();
        entryP1e1.setPrescription(prescription1);
        PrescriptionScheduleEntry entryP1e2 = aPrescriptionScheduleEntry().withDayStage(DAYSTAGE.BEDTIME).withId(2L).build();
        entryP1e2.setPrescription(prescription1);

        PrescriptionScheduleEntry entryP2E3 = aPrescriptionScheduleEntry().withDayStage(DAYSTAGE.WAKEUP).withId(3L).build();
        entryP2E3.setPrescription(prescription2);
        PrescriptionScheduleEntry entryP2e4 = aPrescriptionScheduleEntry().withDayStage(DAYSTAGE.BEDTIME).withId(4L).build();
        entryP2e4.setPrescription(prescription2);

        List<PrescriptionScheduleEntry> pse = List.of(
                entryP1e1,
                entryP1e2,
                entryP2E3,
                entryP2e4);


        DailyEvaluation evaluation = aDailyEvaluation().withRecordDate(requestDate).with(patientBuilder).build();

        List<Dose> existingDoses = DoseBuilder.dosesFor(pse, evaluation);


        given(userService.findByLogin(patient.getUsername()))
                .willReturn(patient);
        given(dailyEvaluationMapper.toDailyEvaluation(request.getDate(), patient))
                .willReturn(evaluation);
        given(dailyEvaluationRepository.findById(evaluation.getDailyEvaluationIdClass()))
                .willReturn(Optional.empty());
        given(dailyEvaluationRepository.save(evaluation))
                .willReturn(evaluation);
        given(prescriptionsService.getPrescriptionsValidOnDate(patient, request.getDate()))
                .willReturn(List.of(prescription1, prescription2));
        given(doseRepository.findByEvaluation(evaluation))
                .willReturn(existingDoses);

        given(prescriptionScheduleEntryRepository.findByPrescriptionIds(
                Stream.of(prescription1, prescription2).map(Prescription::getId).toList()))
                .willReturn(pse);

        given(doseMapper.dailyMedicationDoseDataList(pse, existingDoses))
                .willReturn(DoseMapper.DailyMedicationDoseDataList(pse, existingDoses));

        // when - action or the behaviour that we are going test
        GetDailyDoseDataRequestResponse response = doseService.getDailyDoseData(request, patient.getUsername());
        System.out.println(response);

        // then - verify the output
        assertThat(response).isNotNull();
        assertThat(response.getMedicationDoses().isEmpty()).isFalse();
        assertThat(response.getMedicationDoses().size()).isEqualTo(2);
        assertThat(response.getMedicationDoses().stream()
                .allMatch(dailyMedicationDoseData ->
                        dailyMedicationDoseData.getDoses().stream()
                                .allMatch(dailyDoseData -> dailyDoseData.getDoseId() != null)))
                .isTrue();
        assertThat(response.getResponseInfo().isSuccessful()).isTrue();

    }

    @DisplayName("Test Dose template creation when persisted daily dose data does NOT exist")
    @Test
    public void givenGetDailyDoseDataRequestResponse_whenGetDailyDoseDataForNewDay_thenReturnGetDailyDoseDataResponseWithDoseData() {
        //given - precondition or setup
        LocalDate requestDate = LocalDate.now();
        GetDailyDoseDataRequest request = GetDailyDoseDataRequest.builder().date(requestDate).build();

        UserModelBuilder patientBuilder = UserModelBuilder.aUserModel().withRole(USERROLE.PATIENT);
        UserModel patient = patientBuilder.build();

        MedicationBuilder med1 = MedicationBuilder.aMedication().withId(1L).withName("Med1");
        MedicationBuilder med2 = MedicationBuilder.aMedication().withId(2L).withName("Med2");

        Prescription prescription1 = aPrescription().withId(1L)
                .with(med1)
                .withBeginTime(LocalDateTime.of(requestDate.plusDays(-1), LocalTime.now()))
                .build();
        Prescription prescription2 = aPrescription().withId(2L)
                .with(med2)
                .withBeginTime(LocalDateTime.of(requestDate.plusDays(-1), LocalTime.now()))
                .build();

        prescription1.setPatient(patient);
        prescription2.setPatient(patient);

        PrescriptionScheduleEntry entryP1e1 = aPrescriptionScheduleEntry().withDayStage(DAYSTAGE.WAKEUP).withId(1L).build();
        entryP1e1.setPrescription(prescription1);
        PrescriptionScheduleEntry entryP1e2 = aPrescriptionScheduleEntry().withDayStage(DAYSTAGE.BEDTIME).withId(2L).build();
        entryP1e2.setPrescription(prescription1);

        PrescriptionScheduleEntry entryP2E3 = aPrescriptionScheduleEntry().withDayStage(DAYSTAGE.WAKEUP).withId(3L).build();
        entryP2E3.setPrescription(prescription2);
        PrescriptionScheduleEntry entryP2e4 = aPrescriptionScheduleEntry().withDayStage(DAYSTAGE.BEDTIME).withId(4L).build();
        entryP2e4.setPrescription(prescription2);

        List<PrescriptionScheduleEntry> pse = List.of(
                entryP1e1,
                entryP1e2,
                entryP2E3,
                entryP2e4);


        DailyEvaluation evaluation = aDailyEvaluation().withRecordDate(requestDate).with(patientBuilder).build();

        given(userService.findByLogin(patient.getUsername()))
                .willReturn(patient);
        given(dailyEvaluationMapper.toDailyEvaluation(request.getDate(), patient))
                .willReturn(evaluation);
        given(dailyEvaluationRepository.findById(evaluation.getDailyEvaluationIdClass()))
                .willReturn(Optional.empty());
        given(dailyEvaluationRepository.save(evaluation))
                .willReturn(evaluation);
        given(prescriptionsService.getPrescriptionsValidOnDate(patient, request.getDate()))
                .willReturn(List.of(prescription1, prescription2));
        given(doseRepository.findByEvaluation(evaluation))
                .willReturn(List.of());
        given(prescriptionScheduleEntryRepository.findByPrescriptionIds(Stream.of(prescription1, prescription2).map(Prescription::getId).toList()))
                .willReturn(pse);

        given(doseMapper.dailyMedicationDoseDataList(pse, List.of()))
                .willReturn(DoseMapper.DailyMedicationDoseDataList(pse, List.of()));


        // when - action or the behaviour that we are going test
        GetDailyDoseDataRequestResponse response = doseService.getDailyDoseData(request, patient.getUsername());

        System.out.println(response);
        // then - verify the output
        assertThat(response).isNotNull();
        assertThat(response.getMedicationDoses().isEmpty()).isFalse();
        assertThat(response.getMedicationDoses().size()).isEqualTo(2);
        assertThat(response.getMedicationDoses().stream()
                .allMatch(dailyMedicationDoseData ->
                        dailyMedicationDoseData.getDoses().stream()
                                .allMatch(dailyDoseData -> dailyDoseData.getDoseId() == null)))
                .isTrue();
        assertThat(response.getResponseInfo().isSuccessful()).isTrue();

    }

    @DisplayName("Updating a valid DailyDoseData will return a success response")
    @Test
    public void givenUpdateValidDoseDataEntry_whenAddDailyDoseData_thenReturnSuccessResponse() {
        //given - precondition or setup
        AddDailyDoseDataRequest request = AddDailyDoseDataRequest.builder()
                .date(LocalDate.now().plusDays(1))
                .dailyDoseData(DailyDoseData.builder()
                        .prescriptionScheduleEntryId(1L)
                        .build())
                .build();

        Dose existingDose = aDose().withId(1L).build();

        DailyEvaluation evaluation = existingDose.getEvaluation();
        UserModel patient = evaluation.getUserModel();
        PrescriptionScheduleEntry prescriptionScheduleEntry = existingDose.getPrescriptionScheduleEntry();

        Dose addOrUpdateDose = DoseMapper.Dose(request, evaluation, prescriptionScheduleEntry);

        given(userService.findByLogin(patient.getUsername()))
                .willReturn(patient);
        given(dailyEvaluationMapper.toDailyEvaluation(request.getDate(), patient))
                .willReturn(evaluation);
        given(dailyEvaluationRepository.findById(evaluation.getDailyEvaluationIdClass()))
                .willReturn(Optional.of(evaluation));
        given(prescriptionScheduleEntryRepository.findById(request.getDailyDoseData().getPrescriptionScheduleEntryId()))
                .willReturn(Optional.of(prescriptionScheduleEntry));

        given(doseRepository.findByPrescriptionScheduleEntryAndEvaluation(prescriptionScheduleEntry, evaluation)).willReturn(List.of(existingDose));
        given(doseMapper.dose(request, evaluation, prescriptionScheduleEntry)).willReturn(addOrUpdateDose);
        given(doseRepository.save(addOrUpdateDose)).willReturn(addOrUpdateDose);

        // when - action or the behaviour that we are going test
        AddDailyDoseDataRequestResponse response = doseService.addDailyDoseData(request, patient.getUsername());

        // then - verify the output
        assertThat(response.getResponseInfo().isSuccessful()).isTrue();
    }

    @DisplayName("Adding a valid DailyDoseData will return a success response")
    @Test
    public void givenAddValidNewDoseDataEntry_whenAddDailyDoseData_thenReturnFailureResponse() {
        //given - precondition or setup
        AddDailyDoseDataRequest request = AddDailyDoseDataRequest.builder()
                .date(LocalDate.now().plusDays(1))
                .dailyDoseData(DailyDoseData.builder()
                        .prescriptionScheduleEntryId(1L)
                        .build())
                .build();

        Dose existingDose = aDose().withId(1L).build();

        DailyEvaluation evaluation = existingDose.getEvaluation();
        UserModel patient = evaluation.getUserModel();
        PrescriptionScheduleEntry prescriptionScheduleEntry = existingDose.getPrescriptionScheduleEntry();

        Dose addOrUpdateDose = DoseMapper.Dose(request, evaluation, prescriptionScheduleEntry);

        given(userService.findByLogin(patient.getUsername()))
                .willReturn(patient);
        given(dailyEvaluationMapper.toDailyEvaluation(request.getDate(), patient))
                .willReturn(evaluation);
        given(dailyEvaluationRepository.findById(evaluation.getDailyEvaluationIdClass()))
                .willReturn(Optional.of(evaluation));
        given(prescriptionScheduleEntryRepository.findById(request.getDailyDoseData().getPrescriptionScheduleEntryId()))
                .willReturn(Optional.of(prescriptionScheduleEntry));

        given(doseRepository.findByPrescriptionScheduleEntryAndEvaluation(prescriptionScheduleEntry, evaluation))
                .willReturn(List.of());
        given(doseMapper.dose(request, evaluation, prescriptionScheduleEntry))
                .willReturn(addOrUpdateDose);
        given(doseRepository.save(addOrUpdateDose))
                .willReturn(addOrUpdateDose);
        // when - action or the behaviour that we are going test
        AddDailyDoseDataRequestResponse response = doseService.addDailyDoseData(request, patient.getUsername());

        // then - verify the output
        assertThat(response.getResponseInfo().isSuccessful()).isTrue();
    }


    @DisplayName("Invalid DailyDoseData will throw DailyDoseDataException")
    @Test
    public void givenInValidNewDoseDataEntry_whenAddDailyDoseData_thenReturnFailureResponse() {
        //given - precondition or setup
        AddDailyDoseDataRequest request = AddDailyDoseDataRequest.builder()
                .date(LocalDate.now())
                .dailyDoseData(DailyDoseData.builder()
                        .prescriptionScheduleEntryId(1L)
                        .build())
                .build();
        PrescriptionBuilder prescriptionBuilder = aPrescription().withBeginTime(LocalDateTime.now().plusDays(5));

        Dose existingDose = aDose()
                .withId(1L)
                .withPrescriptionScheduleEntryBuilder(aPrescriptionScheduleEntry()
                        .with(prescriptionBuilder))
                .build();

        DailyEvaluation evaluation = existingDose.getEvaluation();
        UserModel patient = evaluation.getUserModel();

        PrescriptionScheduleEntry prescriptionScheduleEntry = existingDose.getPrescriptionScheduleEntry();

        Dose addOrUpdateDose = DoseMapper.Dose(request, evaluation, prescriptionScheduleEntry);
        given(userService.findByLogin(patient.getUsername()))
                .willReturn(patient);
        given(dailyEvaluationMapper.toDailyEvaluation(request.getDate(), patient))
                .willReturn(evaluation);
        given(dailyEvaluationRepository.findById(evaluation.getDailyEvaluationIdClass()))
                .willReturn(Optional.of(evaluation));
        given(prescriptionScheduleEntryRepository.findById(request.getDailyDoseData().getPrescriptionScheduleEntryId()))
                .willReturn(Optional.of(prescriptionScheduleEntry));
        given(doseRepository.findByPrescriptionScheduleEntryAndEvaluation(prescriptionScheduleEntry, evaluation))
                .willReturn(List.of(existingDose));
        given(doseMapper.dose(request, evaluation, prescriptionScheduleEntry))
                .willReturn(addOrUpdateDose);
        // when - action or the behaviour that we are going test
        assertThrows(DailyDoseDataException.class, () -> doseService.addDailyDoseData(request, patient.getUsername()));
//        AddDailyDoseDataRequestResponse response = doseService.addDailyDoseData(request, patient.getUsername());

        // then - verify the output
//        assertThat(response.getResponseInfo().isSuccessful()).isFalse();
        verify(doseRepository, never()).save(any(Dose.class));
    }


}