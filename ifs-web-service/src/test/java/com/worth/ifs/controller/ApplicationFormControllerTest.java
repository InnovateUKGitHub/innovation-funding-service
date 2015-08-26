package com.worth.ifs.controller;

import com.worth.ifs.domain.Application;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations="classpath:application.properties")
public class ApplicationFormControllerTest  extends BaseUnitTest{

    @InjectMocks
    private ApplicationFormController applicationFormController;


    @Before
    public void setUp(){
        super.setup();

        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(applicationFormController)
                .setViewResolvers(viewResolver())
                .build();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
    }

    @Test
    public void testApplicationForm() throws Exception {
            Application app = applications.get(0);

            when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
            when(applicationService.getApplicationById(app.getId())).thenReturn(app);

            mockMvc.perform(get("/application-form/1"))
                    .andExpect(view().name("application-form"))
                    .andExpect(model().attribute("currentApplication", app))
                    .andExpect(model().attribute("currentSectionId", 0L));

    }

    @Test
    public void testApplicationFormWithOpenSection() throws Exception {
        Application app = applications.get(0);

        when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
        when(applicationService.getApplicationById(app.getId())).thenReturn(app);


        mockMvc.perform(get("/application-form/1/section/1"))
                .andExpect(view().name("application-form"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("currentSectionId", 1L));

    }

    @Test
    public void testAddAnother() throws Exception {

    }

    @Test
    public void testApplicationFormSubmit() throws Exception {

    }

    @Test
    public void testSaveFormElement() throws Exception {

    }
}