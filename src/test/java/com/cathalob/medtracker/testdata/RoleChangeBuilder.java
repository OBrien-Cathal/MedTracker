package com.cathalob.medtracker.testdata;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.userroles.RoleChange;

import java.time.LocalDateTime;

public class RoleChangeBuilder {
    private Long id;
    private USERROLE newRole = USERROLE.PRACTITIONER;
    private USERROLE oldRole = USERROLE.USER;
    private UserModelBuilder userModelBuilder = UserModelBuilder.aUserModel();
    private UserModelBuilder approvedByUserModelBuilder;
    //    private UserModelBuilder approvedByUserModelBuilder = UserModelBuilder.aUserModel().withId(2L).withRole(USERROLE.ADMIN);
    private LocalDateTime requestTime = LocalDateTime.now();
    private LocalDateTime approvalTime;
//    private LocalDateTime approvalTime = LocalDateTime.now().plusDays(1L);

    public RoleChangeBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public RoleChangeBuilder withNewRole(USERROLE newRole) {
        this.newRole = newRole;
        return this;
    }

    public RoleChangeBuilder withOldRole(USERROLE oldRole) {
        this.oldRole = oldRole;
        return this;
    }

    public RoleChangeBuilder withUserModelBuilder(UserModelBuilder userModelBuilder) {
        this.userModelBuilder = userModelBuilder;
        return this;
    }

    public RoleChangeBuilder withApprovedByUserModelBuilder(UserModelBuilder approvedByUserModelBuilder) {
        this.approvedByUserModelBuilder = approvedByUserModelBuilder;
        return this;
    }

    public RoleChangeBuilder withRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
        return this;
    }

    public RoleChangeBuilder withApprovalTime(LocalDateTime approvalTime) {
        this.approvalTime = approvalTime;
        return this;
    }

    public static RoleChangeBuilder aRoleChange() {
        return new RoleChangeBuilder();
    }

    public RoleChangeBuilder() {
    }

    public RoleChangeBuilder(RoleChangeBuilder copy) {
        this.id = copy.id;
        this.newRole = copy.newRole;
        this.oldRole = copy.oldRole;
        this.userModelBuilder = copy.userModelBuilder;
        this.approvedByUserModelBuilder = copy.approvedByUserModelBuilder;
        this.requestTime = copy.requestTime;
        this.approvalTime = copy.approvalTime;

    }

    public RoleChangeBuilder but() {
        return new RoleChangeBuilder();
    }

    public RoleChange build() {
        UserModel userModel = (approvedByUserModelBuilder != null) ? approvedByUserModelBuilder.build() : null;
        return new RoleChange(id,
                newRole,
                oldRole,
                userModelBuilder.build(),
                userModel,
                requestTime,
                approvalTime);
    }

}
