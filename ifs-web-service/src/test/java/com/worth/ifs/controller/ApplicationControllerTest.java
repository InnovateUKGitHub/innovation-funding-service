package com.worth.ifs.controller;

import com.worth.ifs.domain.Application;
import com.worth.ifs.domain.Response;
import com.worth.ifs.domain.Section;
import com.worth.ifs.exception.ObjectNotFoundException;
import com.worth.ifs.service.ApplicationService;
import com.worth.ifs.service.ResponseService;
import com.worth.ifs.service.SectionService;
import com.worth.ifs.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
        when(applicationService.getApplicationById(app.getId())).thenReturn(app);


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
        when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
        when(applicationService.getApplicationById(app.getId())).thenReturn(app);

        mockMvc.perform(get("/application/" + app.getId()+"/summary"))
                .andExpect(status().isOk())
                .andExpect(view().name("application-summary"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("currentCompetition", app.getCompetition()));
    }

    public void testNotExistingApplicationDetails() throws Exception {
        Application app = applications.get(0);

        when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
        when(applicationService.getApplicationById(app.getId())).thenReturn(app);

        System.out.println("Show dashboard for application: " + app.getId());
        mockMvc.perform(get("/application/1234"))
                .andExpect(view().name("404"));
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

    @Test
    public void testApplicationConfirmSubmit() throws Exception {
            Application app = applications.get(0);

            when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
            when(applicationService.getApplicationById(app.getId())).thenReturn(app);

            mockMvc.perform(get("/application/1/confirm-submit"))
                    .andExpect(view().name("application-confirm-submit"))
                    .andExpect(model().attribute("currentApplication", app));

    }

    @Test
    public void testApplicationSubmit() throws Exception {
        Application app = applications.get(0);


        when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
        when(applicationService.getApplicationById(app.getId())).thenReturn(app);

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