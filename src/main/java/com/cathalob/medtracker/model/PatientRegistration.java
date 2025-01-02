package com.cathalob.medtracker.model;

import com.cathalob.medtracker.model.userroles.RoleChange;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity(name = "PATIENT_REGISTRATION")
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class PatientRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USERMODEL_ID", nullable = false)
    private UserModel userModel;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRACTITIONER_ID", nullable = false)
    private UserModel practitionerUserModel;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ROLECHANGE_ID", nullable = false)
    private RoleChange roleChange;


}
