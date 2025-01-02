package com.cathalob.medtracker.controller.api;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.payload.data.RoleChangeData;
import com.cathalob.medtracker.payload.request.RoleChangeApprovalRequest;
import com.cathalob.medtracker.payload.request.RoleChangeRequest;
import com.cathalob.medtracker.payload.response.GenericRequestResponse;
import com.cathalob.medtracker.payload.response.RoleChangeStatusResponse;
import com.cathalob.medtracker.service.UserService;
import com.cathalob.medtracker.service.api.impl.AuthenticationServiceApi;
import com.cathalob.medtracker.service.api.impl.JwtServiceImpl;
import com.cathalob.medtracker.service.impl.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${api.url}")
    private String baseApiUrl;

    @Test
    @WithMockUser("user@user.com")
    public void givenGetUserModelsRequest_when_then() throws Exception {
        //given - precondition or setup
        List<UserModel> users = List.of(aUserModel().withId(1L).build(), aUserModel().withId(2L).build());

        BDDMockito.given(userService.getUserModels()).willReturn(users);
        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(get(getUsersUrlPath()));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }

    @DisplayName("Get Practitioners only returns UserModels with USERROLE PRACTITIONER")
    @Test
    @WithMockUser("user@user.com")
    public void givenGetPractitionersRequest_whenGetPractitioners_thenReturnOnlyPractitioners() throws Exception {
        //given - precondition or setup
        List<UserModel> users = List.of(
                aUserModel().withRole(USERROLE.PRACTITIONER).withId(1L).build(),
                aUserModel().withRole(USERROLE.PRACTITIONER).withId(2L).build());

        BDDMockito.given(userService.getPractitionerUserModels()).willReturn(users);
        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(get(getUsersUrlPath() + "/practitioners"));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.[0].role",CoreMatchers.is("PRACTITIONER")) );
    }

    private String getUsersUrlPath() {
        return baseApiUrl + "/users";
    }

    @Test
    @WithMockUser("user@user.com")
    public void givenRoleRequest_when_then() throws Exception {
        //given - precondition or setup
        UserModel userModel = aUserModel().build();
        RoleChangeRequest roleChangeRequest = RoleChangeRequest.builder().newRole(USERROLE.PRACTITIONER).build();
        GenericRequestResponse genericRequestResponse = new GenericRequestResponse(true, "Request pending with ID: 1");
        given(userService.submitRoleChange(roleChangeRequest.getNewRole(), userModel.getUsername()))
                .willReturn(genericRequestResponse);
        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(post(getRoleRequestsUrlPath() + "/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleChangeRequest)));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestSucceeded", CoreMatchers.is(genericRequestResponse.isRequestSucceeded())))
                .andExpect(jsonPath("$.message", CoreMatchers.is(genericRequestResponse.getMessage())));
    }

    @DisplayName("Non existing role name causes unhandled exception")
    @Test
    @WithMockUser("user@user.com")
    public void givenBogusRoleName_when_then() throws Exception {
        //given - precondition or setup
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("newRole", "foo");

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(post(getRoleRequestsUrlPath() + "/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject.toString()));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isInternalServerError());
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
        ResultActions usersResponse = mockMvc.perform(post(getRoleRequestsUrlPath() + "/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleChangeApprovalRequest)));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestSucceeded", CoreMatchers.is(genericRequestResponse.isRequestSucceeded())))
                .andExpect(jsonPath("$.message", CoreMatchers.is(genericRequestResponse.getMessage())));
    }

    @DisplayName("Get Role change status returns ok and status object")
    @Test
    @WithMockUser("user@user.com")
    public void givenRoleChangeOrNone_whenGetRoleChangeStatus_thenReturnRoleChangeStatusResponse() throws Exception {
        //given - precondition or setup
        UserModel userModel = aUserModel().build();

        RoleChangeStatusResponse roleChangeStatusResponse = new RoleChangeStatusResponse(
                new RoleChangeData(null, "", USERROLE.PRACTITIONER, null),
                new RoleChangeData(null, "", USERROLE.ADMIN, null));
        given(userService.getRoleChangeStatus(userModel.getUsername()))
                .willReturn(roleChangeStatusResponse);
        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(get(getRoleRequestsUrlPath() + "/status")
                .contentType(MediaType.APPLICATION_JSON));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.practitionerRoleChange.id").doesNotExist())
                .andExpect(jsonPath("$.practitionerRoleChange.userRole", CoreMatchers.is("PRACTITIONER")));
    }

    @DisplayName("Get Unapproved role changes returns ok and list of role changes")
    @Test
    @WithMockUser("user@user.com")
    public void givenUnapprovedRoleChanges_whenGetUnapprovedRoleChanges_thenReturnRoleChangeDataList() throws Exception {
        //given - precondition or setup
        UserModel userModel = aUserModel().build();
        List<RoleChangeData> roleChangeDataList = List.of(new RoleChangeData(), new RoleChangeData());
        given(userService.getUnapprovedRoleChanges())
                .willReturn(roleChangeDataList);
        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(get(getRoleRequestsUrlPath() + "/unapproved")
                .contentType(MediaType.APPLICATION_JSON));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }

    private String getRoleRequestsUrlPath() {
        return getUsersUrlPath() + "/role-requests";
    }


}