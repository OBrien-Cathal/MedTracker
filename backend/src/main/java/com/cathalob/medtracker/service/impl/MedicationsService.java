package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.exception.validation.medication.MedicationValidationException;
import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.payload.response.AddMedicationResponse;
import com.cathalob.medtracker.repository.MedicationRepository;
import com.cathalob.medtracker.validate.model.MedicationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class MedicationsService {

    private final MedicationRepository medicationRepository;

    public List<Medication> getMedications() {
        return medicationRepository.findAll();
    }

    public AddMedicationResponse addMedication(Medication medication) throws MedicationValidationException {
        validateMedication(medication);
        Medication saved = medicationRepository.save(medication);

        return AddMedicationResponse.Success(saved.getId());
    }

    private void validateMedication(Medication medication) {
        List<Medication> existing = medicationRepository.findByName(medication.getName());
        MedicationValidator validator = MedicationValidator.aMedicationValidator();
        validator.validateMedication(medication, existing);

        if (!validator.isValid())
            throw new MedicationValidationException(validator.getErrors());
    }
}
