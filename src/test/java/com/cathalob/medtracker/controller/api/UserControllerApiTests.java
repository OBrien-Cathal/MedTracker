package com.cathalob.medtracker.controller.api;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.payload.response.GenericRequestResponse;
import com.cathalob.medtracker.service.UserService;
import com.cathalob.medtracker.service.api.impl.AuthenticationServiceApi;
import com.cathalob.medtracker.service.api.impl.JwtServiceImpl;
import com.cathalob.medtracker.service.impl.CustomUserDetailsService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = UserControllerApi.class)
class UserControllerApiTests {
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
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
        UserModel userModel = aUserModel().build();
        GenericRequestResponse genericRequestResponse = new GenericRequestResponse(true, "Request pending with ID: 1");
        given(userService.submitRoleChange("PRACTITIONER", userModel.getUsername()))
                .willReturn(genericRequestResponse);
        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(post("/api/v1/user/role-request/{ROLE_NAME}", "PRACTITIONER"));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestSucceeded", CoreMatchers.is(genericRequestResponse.isRequestSucceeded())))
                .andExpect(jsonPath("$.message", CoreMatchers.is(genericRequestResponse.getMessage())));
    }
    @Disabled("Ensure role name is a real one")
      @Test
          public void givenBogusRoleName_when_then(){
              //given - precondition or setup

              // when - action or the behaviour that we are going test

              // then - verify the output
          }
    @Disabled("Prevent many role changes for same user, for the same type of role")
      @Test
          public void givenMultipleRoleChangeRequests_when_then(){
              //given - precondition or setup

              // when - action or the behaviour that we are going test

              // then - verify the output
          }

}