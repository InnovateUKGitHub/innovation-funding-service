package com.worth.ifs.controller;

import com.worth.ifs.domain.User;
import com.worth.ifs.security.TokenAuthenticationService;
import com.worth.ifs.security.UserAuthentication;
import org.junit.Before;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class BaseUnitTest {
    protected MockMvc mockMvc;
    User loggedInUser;
    UserAuthentication loggedInUserAuthentication;

    @Mock
    TokenAuthenticationService tokenAuthenticationService;

    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/resources");
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

    @Before
    public void setupBase(){
        loggedInUser = new User(1L, "Nico Bijl", "", "tokenABC", new ArrayList());
        loggedInUserAuthentication = new UserAuthentication(loggedInUser);
    }

    public void loginDefaultUser(){
        when(tokenAuthenticationService.getAuthentication(any(HttpServletRequest.class))).thenReturn(loggedInUserAuthentication);
    }
}
