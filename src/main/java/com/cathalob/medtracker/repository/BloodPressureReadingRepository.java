package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloodPressureReadingRepository extends JpaRepository<BloodPressureReading, Long> {
}
