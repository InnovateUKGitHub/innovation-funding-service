package com.worth.ifs.controller;

import com.worth.ifs.domain.*;
import com.worth.ifs.security.TokenAuthenticationService;
import com.worth.ifs.security.UserAuthentication;
import com.worth.ifs.service.ApplicationService;
import com.worth.ifs.service.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


//@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
//@ContextConfiguration
public class ApplicantControllerTest extends BaseUnitTest {

    @InjectMocks
    private ApplicantController applicantController;


    @Mock
    ApplicationService applicationService;



    @Before
    public void setUp() {
        super.setup();
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(applicantController)
                .setViewResolvers(viewResolver())
                .build();


        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
    }


    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testDashboard() throws Exception {
        when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);

        mockMvc.perform(get("/applicant/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("applicant-dashboard"))
                .andExpect(model().attribute("applicationsInProcess", hasSize(2)))
                .andExpect(model().attribute("applicationsFinished", hasSize(2)));

    }
}