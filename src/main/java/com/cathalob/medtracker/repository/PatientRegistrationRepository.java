package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRegistrationRepository extends JpaRepository<PatientRegistration, Long> {
    List<PatientRegistration> findByPractitionerUserModel(UserModel practitioner);
}
