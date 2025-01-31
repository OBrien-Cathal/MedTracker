package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface UserModelRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByUsername(String username);
    List<UserModel> findByRole(USERROLE role);
    @Query( "select o from USERMODEL o where o.id in :ids" )
    List<UserModel> findByUserModelIds( List<Long> ids);

}
