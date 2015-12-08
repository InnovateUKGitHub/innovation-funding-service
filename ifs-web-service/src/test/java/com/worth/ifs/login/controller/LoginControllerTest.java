package com.worth.ifs.login.controller;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.login.LoginController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations="classpath:application.properties")
public class LoginControllerTest extends BaseUnitTest {

    @InjectMocks
    private LoginController loginController;

    @Before
    public void setUp(){
        super.setup();
        setupUserRoles();

        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(loginController)
                .setViewResolvers(viewResolver())
                .build();
    }


    /**
     * Test if the login view shows the user accounts to login with.
     */
    @Test
    public void testLoginViewWithUsers() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    /**
     * Test if the login works, when we enter a valid login token.
     */
    @Test
    public void testSubmitValidLogin() throws Exception {
        String loginToken = "token1";
        MvcResult result = this.performLogin(loginToken)
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/applicant/dashboard"))
                        // .andExpect(cookie().exists("IFS_AUTH_TOKEN"))
                .andReturn();

    }

    private ResultActions performLogin(String loginToken) throws Exception {
        String userPass = "test";
        when(userAuthenticationService.authenticate(loggedInUser.getEmail(), userPass)).thenReturn(loggedInUser);
        return mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", loggedInUser.getEmail())
                        .param("password", userPass)
        );
    }

    /**
     * Test if, when the users submits the form with a invalid token, the user is shown the login page again.
     */
    @Test
    public void testSubmitInvalidLogin() throws Exception {
        when(userAuthenticationService.authenticate("info@test.nl", "testFOUT")).thenThrow(new BadCredentialsException("Invalid username / password"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "info@test.nl")
                        .param("password", "testFOUT")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("/login"))
                .andExpect(model().attributeHasFieldErrors("loginForm", "password"));
    }

    @Test
    public void testSubmitInvalidEmail() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "infotest.nl")
                        .param("password", "testFOUT")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("/login"))
                .andExpect(model().attributeHasFieldErrors("loginForm", "email"));
    }

    @Test
    public void testSubmitWithoutEmail() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "")
                        .param("password", "test")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("/login"))
                .andExpect(model().attributeHasFieldErrors("loginForm", "email"));
    }

    @Test
    public void testSubmitWithoutPassword() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "info@test.nl")
                        .param("password", "")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("/login"))
                .andExpect(model().attributeHasFieldErrors("loginForm", "password"));
    }

    @Test
    public void testLogout() throws Exception {
        this.loginDefaultUser();
        mockMvc.perform(get("/login").param("logout", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"));

        System.out.println("Testing Logout...");
    }

    @Test
    public void testRedirectToCreateApplicationWithValidLogin() throws Exception {
        String userPass = "test";
        when(userAuthenticationService.authenticate(loggedInUser.getEmail(), userPass)).thenReturn(loggedInUser);
        mockMvc.perform(post("/login?competitionId=1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", loggedInUser.getEmail())
                .param("password", userPass))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/application/create/1"))
                .andReturn();

    }

    @Test
    public void testRedirectToCreateApplicationWithInvalidLogin() throws Exception {
        when(userAuthenticationService.authenticate("info@test.nl", "testFOUT")).thenThrow(new BadCredentialsException("Invalid username / password"));
        mockMvc.perform(post("/login?competitionId=1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", "info@test.nl")
                .param("password", "testFOUT"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("/login"))
                .andExpect(model().attribute("loginForm", hasProperty("actionUrl", is("/login?competitionId=1"))))
                .andReturn();
    }

    @Test
    public void testLoginPageWithStringAsCompetitionId() throws Exception {
        when(userAuthenticationService.authenticate("info@test.nl", "testFOUT")).thenThrow(new BadCredentialsException("Invalid username / password"));
        mockMvc.perform(get("/login?competitionId=abcdef")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", "info@test.nl")
                .param("password", "testFOUT"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("loginForm", hasProperty("actionUrl", is("/login"))))
                .andReturn();
    }

    @Test
    public void testLoginPageWithNegativeIntegerAsCompetitionId() throws Exception {
        mockMvc.perform(get("/login?competitionId=-10")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("loginForm", hasProperty("actionUrl", is("/login"))))
                .andReturn();
    }
}