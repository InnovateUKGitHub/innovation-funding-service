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
    UserService userServiceMock;

    @Mock
    ApplicationService applicationService;


    private List<Application> applications;

    @Before
    public void setUp() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(applicantController)
                .setViewResolvers(viewResolver())
                .build();



        Competition comp = new Competition(1L, "Competition x", "Description afds", new Date());
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