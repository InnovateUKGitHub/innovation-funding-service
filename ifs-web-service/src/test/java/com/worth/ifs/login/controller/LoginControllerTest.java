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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
//        User user1 = new User(1L, "Nico Bijl", "email","password", "token1", "image", null);
//        User user2 = new User(2L, "Rogier de Regt", "email2@email.nl","password", "token2", "image", null);
//        User user3 = new User(3L, "Wouter de Meijer", "email3@email.nl","password", "token3", "image", null);
//        when(userServiceMock.findAll()).thenReturn(Arrays.asList(user1, user2, user3));

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
        //when(userService.retrieveUserByEmailAndPassword(loggedInUser.getEmail(), userPass)).thenReturn(loggedInUser);
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
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login?invalid"));
    }




    @Test
    public void testLogout() throws Exception {
        this.loginDefaultUser();
        //when(userService.retrieveUserByEmailAndPassword(loggedInUser.getEmail(), "test")).thenReturn(loggedInUser);


                mockMvc.perform(get("/login").param("logout", "true"))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(view().name("redirect:/login"));

        System.out.println("Testing Logout...");
    }

}