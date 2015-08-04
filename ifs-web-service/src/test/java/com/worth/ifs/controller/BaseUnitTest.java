package com.worth.ifs.controller;

import com.worth.ifs.domain.*;
import com.worth.ifs.security.TokenAuthenticationService;
import com.worth.ifs.security.UserAuthentication;
import org.junit.Before;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class BaseUnitTest {
    public MockMvc mockMvc;
    public User loggedInUser;
    public UserAuthentication loggedInUserAuthentication;

    @Mock
    TokenAuthenticationService tokenAuthenticationService;
    public List<Application> applications;
    public Competition comp;

    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/resources");
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

    public void setup(){
        loggedInUser = new User(1L, "Nico Bijl", "", "tokenABC", new ArrayList());
        loggedInUserAuthentication = new UserAuthentication(loggedInUser);
    }

    public void loginDefaultUser(){
        when(tokenAuthenticationService.getAuthentication(any(HttpServletRequest.class))).thenReturn(loggedInUserAuthentication);
    }

    public void setupCompetition(){
        comp = new Competition(1L, "Competition x", "Description afds", new Date());

    }
    public void setupApplicationWithRoles(){
        Application app1 = new Application(1L, "Rovel Additive Manufacturing Process", new ProcessStatus(1L, "created"));
        Application app2 = new Application(2L, "Providing sustainable childcare", new ProcessStatus(2L, "submitted"));
        Application app3 = new Application(3L, "Mobile Phone Data for Logistics Analytics", new ProcessStatus(3L, "approved"));
        Application app4 = new Application(4L, "Using natural gas to heat homes", new ProcessStatus(4L, "rejected"));
        Role role = new Role(1L, "leadapplicant", null);

        UserApplicationRole userAppRole1 = new UserApplicationRole(1L, loggedInUser, app1, role);
        UserApplicationRole userAppRole2 = new UserApplicationRole(2L, loggedInUser, app2, role);
        UserApplicationRole userAppRole3 = new UserApplicationRole(3L, loggedInUser, app3, role);
        UserApplicationRole userAppRole4 = new UserApplicationRole(4L, loggedInUser, app4, role);

        comp.addApplication(app1, app2, app3, app4);

        app1.setCompetition(comp);
        app1.setUserApplicationRoles(Arrays.asList(userAppRole1));
        app2.setCompetition(comp);
        app2.setUserApplicationRoles(Arrays.asList(userAppRole2));
        app3.setCompetition(comp);
        app3.setUserApplicationRoles(Arrays.asList(userAppRole3));
        app4.setCompetition(comp);
        app4.setUserApplicationRoles(Arrays.asList(userAppRole4));

        loggedInUser.addUserApplicationRole(userAppRole1, userAppRole2, userAppRole3, userAppRole3);
        applications = Arrays.asList(app1, app2, app3, app4);

    }
}
