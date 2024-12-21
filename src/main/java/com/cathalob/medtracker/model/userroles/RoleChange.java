package com.cathalob.medtracker.model.userroles;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "ROLECHANGE")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleChange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private USERROLE newRole;
    @Enumerated(EnumType.STRING)
    private USERROLE oldRole;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USERMODEL_ID", nullable = false)
    private UserModel userModel;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "APPROVED_BY_ID")
    @JsonIgnore
    private UserModel approvedBy;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime requestTime;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime approvalTime;

}
