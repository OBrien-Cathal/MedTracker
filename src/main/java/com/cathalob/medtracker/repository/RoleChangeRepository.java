package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.userroles.RoleChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleChangeRepository extends JpaRepository<RoleChange, Long> {
    @Query("FROM ROLECHANGE e WHERE e.userModel.id = :userModelId AND e.newRole = :newRole AND e.approvedBy is null")
    List<RoleChange> findUnapprovedRoleChange(
            @Param("userModelId") Long userModelId,
            @Param("newRole") USERROLE newRole);

    List<RoleChange> findByUserModelId(Long userModelId);
    List<RoleChange> findByUserModelIdAndNewRoleAndApprovedById(Long userModelId, USERROLE newRole, Long approvedBy);
    List<RoleChange> findByApprovedById(Long approvedBy);

}
