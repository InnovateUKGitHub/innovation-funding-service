package com.worth.ifs.controller;

import com.worth.ifs.domain.Application;
import com.worth.ifs.domain.Section;
import com.worth.ifs.security.TokenAuthenticationService;
import com.worth.ifs.service.ApplicationService;
import com.worth.ifs.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationControllerTest extends BaseUnitTest {
    @InjectMocks
    private ApplicationController applicationController;

    @Mock
    UserService userServiceMock;
    @Mock
    ApplicationService applicationService;


    @Before
    public void setUp(){
        super.setup();
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(applicationController)
                .setViewResolvers(viewResolver())
                .build();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
    }

    @Test
     public void testApplicationDetails() throws Exception {
        Application app = applications.get(0);

        when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
        when(applicationService.getApplicationById(app.getId())).thenReturn(app);

        System.out.println("Show dashboard for application: " + app.getId());
        mockMvc.perform(get("/application/" + app.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-details"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("currentCompetition", app.getCompetition()));
    }

    @Test
    public void testNotExistingApplicationDetails() throws Exception {
        Application app = applications.get(0);

        when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
        when(applicationService.getApplicationById(app.getId())).thenReturn(app);

        System.out.println("Show dashboard for application: "+ app.getId());
        mockMvc.perform(get("/application/1234"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("application-details"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("currentCompetition", app.getCompetition()));
    }

    @Test
    public void testApplicationDetailsOpenSection() throws Exception {
        Application app = applications.get(0);
        Section section = sections.get(2);


        when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
        when(applicationService.getApplicationById(app.getId())).thenReturn(app);

        System.out.println("Show dashboard for application: " + app.getId());
        mockMvc.perform(get("/application/" + app.getId() +"/section/"+ section.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-details"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("currentCompetition", app.getCompetition()))
                .andExpect(model().attribute("sections", sections))
                .andExpect(model().attribute("currentSectionId", section.getId()));

    }
}