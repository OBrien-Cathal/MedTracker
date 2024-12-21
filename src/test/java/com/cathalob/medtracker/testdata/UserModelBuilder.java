package com.cathalob.medtracker.testdata;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;

public class UserModelBuilder {

    private Long id;

    private String username = "user@user.com";
    private USERROLE role = USERROLE.USER;
    private String password = "abc";
    public UserModelBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public UserModelBuilder withUsername(String username){
        this.username = username;
        return this;
    }
    public UserModelBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserModelBuilder withRole(USERROLE role) {
        this.role = role;
        return this;
    }
    public UserModel build(){
        return new UserModel(id, username,password, role);
    }

    public static UserModelBuilder aUserModel(){
        return new UserModelBuilder();
    }

}
