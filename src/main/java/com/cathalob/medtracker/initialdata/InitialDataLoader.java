package com.cathalob.medtracker.initialdata;

import com.cathalob.medtracker.fileupload.BloodPressureFileImporter;
import com.cathalob.medtracker.fileupload.DoseFileImporter;
import com.cathalob.medtracker.fileupload.ImportCache;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;

import com.cathalob.medtracker.service.impl.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@Component
public class InitialDataLoader implements ApplicationRunner {
    private final ImportCache importCache;
    private final EvaluationDataService evaluationDataService;
    private final PatientsService patientsService;
    private final PrescriptionsService prescriptionsService;
    private final UserServiceImpl userService;
    private final DoseService doseService;

    public InitialDataLoader(EvaluationDataService evaluationDataService, PatientsService patientsService, PrescriptionsService prescriptionsService, UserServiceImpl userService, DoseService doseService) {
        this.importCache = new ImportCache();
        this.evaluationDataService = evaluationDataService;
        this.patientsService = patientsService;
        this.prescriptionsService = prescriptionsService;
        this.userService = userService;
        this.doseService = doseService;
    }

    @Override
    public void run(ApplicationArguments args) {
        importCache.loadMedications(prescriptionsService);
//        basic data from data.sql only contains one medication, if more than one is present then we should not load from file again
        if (importCache.getMedications().containsKey(2L)) return;
        loadDbData();

        importCache.setUserModel(importCache.getUserModels().get(4L));
        processMedicationExcelFile();
        processPrescriptionExcelFile();
        processPrescriptionScheduleEntriesExcelFile();
        processDoseExcelFile();
        processBloodPressureReadingsExcelFile();
    }

    private void loadDbData() {
        importCache.loadUserModels(userService);
        importCache.loadPrescriptions(prescriptionsService);
        importCache.loadPrescriptionScheduleEntries(prescriptionsService);
    }

    public void processMedicationExcelFile() {
        List<Medication> newMedications = new ArrayList<>();
        Workbook workbook = null;

        try {

            FileInputStream fileInputStream = new FileInputStream("./src/main/resources/initialDataFiles/medications.xlsx");
            workbook = new XSSFWorkbook(fileInputStream);
//            log.info("Number of sheets: " + workbook.getNumberOfSheets());

            workbook.forEach(sheet -> {
//                log.info("Title of sheet => " + sheet.getSheetName());

                DataFormatter dataFormatter = new DataFormatter();
                int index = 0;
                for (Row row : sheet) {
                    if (index++ == 0) continue;
                    Medication medication = new Medication();
                    if (row.getCell(0) != null) {
                        medication.setName(dataFormatter.formatCellValue(row.getCell(0)));
                    }
//                    log.info(medication.toString());
                    newMedications.add(medication);
                }
            });
        } catch (EncryptedDocumentException | IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (workbook != null) workbook.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        prescriptionsService.saveMedications(newMedications);
        newMedications.forEach(newMedication ->
                importCache.getMedications().put(newMedication.getId(), newMedication)
        );
    }

    public void processPrescriptionExcelFile() {
        List<Prescription> newPrescriptions = new ArrayList<>();
        XSSFWorkbook workbook = null;

        try {
            FileInputStream fileInputStream = new FileInputStream("./src/main/resources/initialDataFiles/prescriptions.xlsx");
            workbook = new XSSFWorkbook(fileInputStream);
//            log.info("Number of sheets: " + workbook.getNumberOfSheets());

            workbook.forEach(sheet -> {
//                log.info("Title of sheet => " + sheet.getSheetName());
                int index = 0;
                for (Row row : sheet) {
                    if (index++ == 0) continue;
                    Prescription prescription = new Prescription();
                    if (row.getCell(1) != null) {
                        long numericCellValue = (long) ((int) row.getCell(1).getNumericCellValue());
                        Medication medication = importCache.getMedications().get(numericCellValue);

                        prescription.setMedication(medication);
                    }
                    if (row.getCell(2) != null) {
                        long numericCellValue = (long) ((int) row.getCell(2).getNumericCellValue());
                        UserModel userModel = importCache.getUserModels().get(numericCellValue);
                        prescription.setPatient(userModel);
                    }
                    if (row.getCell(3) != null) {
                        long numericCellValue = (long) ((int) row.getCell(3).getNumericCellValue());
                        UserModel userModel = importCache.getUserModels().get(numericCellValue);
                        prescription.setPractitioner(userModel);
                    }

                    if (row.getCell(4) != null) {
                        LocalDateTime localDateTimeCellValue = row.getCell(4).getLocalDateTimeCellValue();
                        prescription.setBeginTime(localDateTimeCellValue);
                    }
                    if (row.getCell(5) != null) {
                        LocalDateTime localDateTimeCellValue = row.getCell(5).getLocalDateTimeCellValue();

                        prescription.setEndTime(localDateTimeCellValue);
                    }
                    if (row.getCell(6) != null) {
                        int localDateTimeCellValue = (int) row.getCell(6).getNumericCellValue();
                        prescription.setDoseMg(localDateTimeCellValue);
                    }

                    newPrescriptions.add(prescription);
                }
            });
        } catch (EncryptedDocumentException | IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (workbook != null) workbook.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        prescriptionsService.savePrescriptions(newPrescriptions);
        newPrescriptions.forEach(prescription -> importCache.getPrescriptions().put(prescription.getId(), prescription));
    }

    public void processPrescriptionScheduleEntriesExcelFile() {
        List<PrescriptionScheduleEntry> newPrescriptionScheduleEntries = new ArrayList<>();

        XSSFWorkbook workbook = null;

        try {
            FileInputStream fileInputStream = new FileInputStream("./src/main/resources/initialDataFiles/prescriptionScheduleEntries.xlsx");
            workbook = new XSSFWorkbook(fileInputStream);
//            log.info("Number of sheets: " + workbook.getNumberOfSheets());

            workbook.forEach(sheet -> {
//                log.info("Title of sheet => " + sheet.getSheetName());

                int index = 0;
                for (Row row : sheet) {
                    if (index++ == 0) continue;
                    PrescriptionScheduleEntry prescriptionScheduleEntry = new PrescriptionScheduleEntry();
                    if (row.getCell(0) != null) {
                        long numericCellValue = (long) ((int) row.getCell(0).getNumericCellValue());
                        Prescription prescription = importCache.getPrescriptions().get(numericCellValue);
                        prescriptionScheduleEntry.setPrescription(prescription);
                    }
                    if (row.getCell(1) != null) {
                        String dayStage = row.getCell(1).getStringCellValue();
                        prescriptionScheduleEntry.setDayStage(DAYSTAGE.valueOf(dayStage));
                    }
                    newPrescriptionScheduleEntries.add(prescriptionScheduleEntry);
                }
            });
        } catch (EncryptedDocumentException | IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (workbook != null) workbook.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        prescriptionsService.savePrescriptionScheduleEntries(newPrescriptionScheduleEntries);
        newPrescriptionScheduleEntries.forEach(prescriptionScheduleEntry -> importCache.getPrescriptionScheduleEntries().put(prescriptionScheduleEntry.getId(), prescriptionScheduleEntry));
    }

    public void processDoseExcelFile() {
        DoseFileImporter doseFileImporter = new DoseFileImporter(evaluationDataService, prescriptionsService, doseService);
        doseFileImporter.setImportCache(importCache);
        doseFileImporter
                .processFileNamed("./src/main/resources/initialDataFiles/doses.xlsx");
    }

    public void processBloodPressureReadingsExcelFile() {
        BloodPressureFileImporter bloodPressureFileImporter = new BloodPressureFileImporter(evaluationDataService, patientsService);
        bloodPressureFileImporter.setImportCache(importCache);
        bloodPressureFileImporter
                .processFileNamed("./src/main/resources/initialDataFiles/bloodPressureReadings.xlsx");
    }
}