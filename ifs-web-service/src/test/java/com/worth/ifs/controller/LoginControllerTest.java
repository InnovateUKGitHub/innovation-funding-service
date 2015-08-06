package com.worth.ifs.controller;

import com.worth.ifs.domain.User;
import com.worth.ifs.security.TokenAuthenticationService;
import com.worth.ifs.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations="classpath:application.properties")
public class LoginControllerTest extends BaseUnitTest{

    @InjectMocks
    private LoginController loginController;

    @Mock
    UserService userServiceMock;

    @Before
    public void setUp(){
        super.setup();

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
        User user1 = new User(1L, "Nico Bijl", "email","password", "token1", "image", null);
        User user2 = new User(2L, "Rogier de Regt", "email2@email.nl","password", "token2", "image", null);
        User user3 = new User(3L, "Wouter de Meijer", "email3@email.nl","password", "token3", "image", null);
        when(userServiceMock.findAll()).thenReturn(Arrays.asList(user1, user2, user3));

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
        this.performLogin(loginToken)
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/applicant/dashboard"))
                // .andExpect(cookie().exists("IFS_AUTH_TOKEN"))
                .andReturn();
    }

    private ResultActions performLogin(String loginToken) throws Exception {
        User user1 = new User(1L, "Nico Bijl", "email@email.nl", "pass123", loginToken, "image", null);
        when(userServiceMock.retrieveUserByEmailAndPassword(user1.getEmail(), user1.getPassword())).thenReturn(user1);

        return mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", user1.getEmail())
                        .param("password", user1.getPassword())
        );

    }

    /**
     * Test if, when the users submits the form with a invalid token, the user is shown the login page again.
     */
    @Test
    public void testSubmitInvalidLogin() throws Exception {
        when(userServiceMock.retrieveUserByEmailAndPassword("info@test.nl", "testFOUT")).thenReturn(null);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "info@test.nl")
                        .param("password", "testFOUT")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login?invalid"));
    }




//    @Test
//    public void testLogout() throws Exception {
//        //this.loginDefaultUser();
//
//
//        mockMvc.perform(get("/logout"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("redirect:/login?logout"));
//
//        mockMvc.perform(get("/login").param("logout", "true"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("redirect:/login"));
//
//        mockMvc.perform(get("/applicant/dashboard"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("applicant-dashboard"));
//
//        System.out.println("Testing Logout...");
//    }

}