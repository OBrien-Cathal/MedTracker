package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.userroles.RoleChange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleChangeRepository extends JpaRepository<RoleChange, Long> {
}
