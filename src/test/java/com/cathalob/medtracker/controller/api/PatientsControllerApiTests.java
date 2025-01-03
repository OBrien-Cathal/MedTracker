package com.cathalob.medtracker.controller.api;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.service.api.impl.AuthenticationServiceApi;
import com.cathalob.medtracker.service.api.impl.JwtServiceImpl;
import com.cathalob.medtracker.service.api.impl.PatientsServiceApi;
import com.cathalob.medtracker.service.impl.CustomUserDetailsService;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@Import(SecurityConfig.class)
@WebMvcTest(controllers = PatientsControllerApi.class)
class PatientsControllerApiTests {
    @MockBean
    private PatientsServiceApi patientsService;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtServiceImpl jwtService;
    @MockBean
    private AuthenticationServiceApi authenticationServiceApi;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Value("${api.url}")
    private String baseApiUrl;

    @DisplayName("Get Patients only returns UserModels with USERROLE PATIENT")
    @Test
    @WithMockUser(value = "user@user.com", roles = {"PRACTITIONER"})
    public void givenGetPatientRequest_whenGetPatient_thenReturnOnlyPatients() throws Exception {
        //given - precondition or setup
        List<UserModel> users = List.of(
                aUserModel().withRole(USERROLE.PATIENT).withId(1L).build(),
                aUserModel().withRole(USERROLE.PATIENT).withId(2L).build());

        BDDMockito.given(patientsService.getPatientUserModels("user@user.com")).willReturn(users);
        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(get(patientsURL()));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.[0].role", CoreMatchers.is("PATIENT")));
    }
    @DisplayName("Get Patients fails with wrong role")
    @Test
    @WithMockUser(value = "user@user.com")
    public void givenGetPatientRequest_whenGetPatientAsUser_thenReturnOnlyPatients() throws Exception {
        //given - precondition or setup

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(get(patientsURL()));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    private String patientsURL() {
        return baseApiUrl + "/patients";
    }

}