package com.worth.ifs.controller;

import com.worth.ifs.security.TokenAuthenticationService;
import com.worth.ifs.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationControllerTest extends BaseUnitTest {

    @InjectMocks
    private ApplicationController applicationController;

    @Mock
    UserService userServiceMock;
    @Mock
    TokenAuthenticationService tokenAuthenticationService;

    @Test
    public void testApplicationDetails() throws Exception {

    }

    @Test
    public void testApplicationDetailsOpenSection() throws Exception {

    }
}