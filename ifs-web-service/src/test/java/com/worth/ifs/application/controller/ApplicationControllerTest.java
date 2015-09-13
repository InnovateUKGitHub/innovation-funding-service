package com.worth.ifs.application.controller;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.application.ApplicationController;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Section;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationControllerTest extends BaseUnitTest {
    @InjectMocks
    private ApplicationController applicationController;


    @Before
    public void setUp(){
        super.setup();
        // Process mock annotations
        MockitoAnnotations.initMocks(this);


        mockMvc = MockMvcBuilders.standaloneSetup(applicationController)
                .setViewResolvers(viewResolver())
                .setHandlerExceptionResolvers(createExceptionResolver())
                .build();


        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
    }



    @Test
     public void testApplicationDetails() throws Exception {
        Application app = applications.get(0);

       // when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
        when(applicationService.getById(app.getId())).thenReturn(app);


        System.out.println("Show dashboard for application: " + app.getId());
        mockMvc.perform(get("/application/" + app.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-details"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("completedSections", Arrays.asList(1L, 2L)))
                .andExpect(model().attribute("incompletedSections", Arrays.asList(3L, 4L)))
                .andExpect(model().attribute("currentCompetition", app.getCompetition()));
    }

    @Test
    public void testApplicationSummary() throws Exception {
        Application app = applications.get(0);
        //when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
        when(applicationService.getById(app.getId())).thenReturn(app);

        mockMvc.perform(get("/application/" + app.getId()+"/summary"))
                .andExpect(status().isOk())
                .andExpect(view().name("application-summary"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("currentCompetition", app.getCompetition()))
                .andExpect(model().attribute("leadOrganisation", organisations.get(0)))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasSize(organisations.size())))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasItem(organisations.get(0))))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasItem(organisations.get(1))));
    }

    public void testNotExistingApplicationDetails() throws Exception {
        Application app = applications.get(0);

        //when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
        when(applicationService.getById(app.getId())).thenReturn(app);

        System.out.println("Show dashboard for application: " + app.getId());
        mockMvc.perform(get("/application/1234"))
                .andExpect(view().name("404"));
    }

    @Test
    public void testApplicationDetailsOpenSection() throws Exception {
        Application app = applications.get(0);
        Section section = sections.get(2);


        //when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
        when(applicationService.getById(app.getId())).thenReturn(app);

        System.out.println("Show dashboard for application: " + app.getId());
        mockMvc.perform(get("/application/" + app.getId() +"/section/"+ section.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-details"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("currentCompetition", app.getCompetition()))
                .andExpect(model().attribute("sections", sections))
                .andExpect(model().attribute("currentSectionId", section.getId()))
                .andExpect(model().attribute("leadOrganisation", organisations.get(0)))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasSize(organisations.size())))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasItem(organisations.get(0))))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasItem(organisations.get(1))));
//        Matchers.hasItems()
    }

    @Test
    public void testApplicationConfirmSubmit() throws Exception {
            Application app = applications.get(0);

            //when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
            when(applicationService.getById(app.getId())).thenReturn(app);

            mockMvc.perform(get("/application/1/confirm-submit"))
                    .andExpect(view().name("application-confirm-submit"))
                    .andExpect(model().attribute("currentApplication", app));

    }

    @Test
    public void testApplicationSubmit() throws Exception {
        Application app = applications.get(0);


        //when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
        when(applicationService.getById(app.getId())).thenReturn(app);

        MvcResult result = mockMvc.perform(get("/application/1/submit"))
                .andExpect(view().name("application-submitted"))
                .andExpect(model().attribute("currentApplication", app))
                .andReturn();

        // TODO: test the application status, but how without having a database in place?
        //        Application updatedApplication = (Application) result.getModelAndView().getModel().get("currentApplication");
        //        String name = updatedApplication.getApplicationStatus().getName();
        //        assertEquals(name, "submitted");
    }
}