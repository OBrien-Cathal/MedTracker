package com.cathalob.medtracker.controller.api;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.payload.response.GenericRequestResponse;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PrescriptionsController.class)
@Import(SecurityConfig.class)
class PrescriptionsControllerTests {
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtServiceImpl jwtService;
    @MockBean
    private AuthenticationServiceApi authenticationServiceApi;

    @Disabled("needs implementation")
    @Test
    @WithMockUser("user@user.com")
    public void givenGetPrescriptionsRequest_whenGetPrescriptions_then() throws Exception {
        //given - precondition or setup
        GenericRequestResponse genericRequestResponse = new GenericRequestResponse(
                true,
                "Stub");

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(get("/api/v1/prescriptions"));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestSucceeded", CoreMatchers.is(genericRequestResponse.isRequestSucceeded())))
                .andExpect(jsonPath("$.message", CoreMatchers.is(genericRequestResponse.getMessage())));
    }
}