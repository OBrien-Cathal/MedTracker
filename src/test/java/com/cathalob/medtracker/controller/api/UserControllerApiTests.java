package com.cathalob.medtracker.controller.api;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.payload.request.RoleChangeApprovalRequest;
import com.cathalob.medtracker.payload.response.GenericRequestResponse;
import com.cathalob.medtracker.service.UserService;
import com.cathalob.medtracker.service.api.impl.AuthenticationServiceApi;
import com.cathalob.medtracker.service.api.impl.JwtServiceImpl;
import com.cathalob.medtracker.service.impl.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = UsersControllerApi.class)
class UserControllerApiTests {
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JwtServiceImpl jwtService;
    @MockBean
    private AuthenticationServiceApi authenticationServiceApi;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser("user@user.com")
    public void givenGetUserModelsRequest_when_then() throws Exception {
        //given - precondition or setup
        List<UserModel> users = List.of(aUserModel().withId(1L).build(), aUserModel().withId(2L).build());

        BDDMockito.given(userService.getUserModels()).willReturn(users);
        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(get("/api/v1/users"));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }
    @Test
    @WithMockUser("user@user.com")
    public void givenRoleRequest_when_then() throws Exception {
        //given - precondition or setup
        UserModel userModel = aUserModel().build();
        GenericRequestResponse genericRequestResponse = new GenericRequestResponse(true, "Request pending with ID: 1");
        given(userService.submitRoleChange("PRACTITIONER", userModel.getUsername()))
                .willReturn(genericRequestResponse);
        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(post("/api/v1/users/role-request/{ROLE_NAME}", "PRACTITIONER"));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestSucceeded", CoreMatchers.is(genericRequestResponse.isRequestSucceeded())))
                .andExpect(jsonPath("$.message", CoreMatchers.is(genericRequestResponse.getMessage())));
    }
    @Disabled("Ensure role name is a real one")
    @Test
    public void givenBogusRoleName_when_then() {
        //given - precondition or setup

        // when - action or the behaviour that we are going test

        // then - verify the output
    }

    @Disabled("Prevent many role changes for same user, for the same type of role")
    @Test
    public void givenMultipleRoleChangeRequests_when_then() {
        //given - precondition or setup

        // when - action or the behaviour that we are going test

        // then - verify the output
    }

    @Test
    @WithMockUser("user@user.com")
    public void givenRoleChangeApproval_whenApproveRoleChange_thenReturnSuccessfulResponse() throws Exception {
        //given - precondition or setup
        UserModel userModel = aUserModel().build();
        RoleChangeApprovalRequest roleChangeApprovalRequest = new RoleChangeApprovalRequest(1L);
        GenericRequestResponse genericRequestResponse = new GenericRequestResponse(true);
        given(userService.approveRoleChange(1L, userModel.getUsername()))
                .willReturn(genericRequestResponse);
        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(post("/api/v1/users/role-request/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleChangeApprovalRequest)));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestSucceeded", CoreMatchers.is(genericRequestResponse.isRequestSucceeded())))
                .andExpect(jsonPath("$.message", CoreMatchers.is(genericRequestResponse.getMessage())));
    }
}