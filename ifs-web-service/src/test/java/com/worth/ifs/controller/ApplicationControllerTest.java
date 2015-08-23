package com.worth.ifs.controller;

import com.worth.ifs.domain.Application;
import com.worth.ifs.domain.Response;
import com.worth.ifs.domain.Section;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationControllerTest extends BaseUnitTest {
    @InjectMocks
    private ApplicationController applicationController;

    @Mock
    UserService userServiceMock;
    @Mock
    ApplicationService applicationService;
    @Mock
    ResponseService responseService;
    @Mock
    SectionService sectionService;

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
        when(sectionService.getCompletedSectionIds(app.getId())).thenReturn(Arrays.asList(1L, 2L));
        when(sectionService.getIncompletedSectionIds(app.getId())).thenReturn(Arrays.asList(3L, 4L));

        System.out.println("Show dashboard for application: " + app.getId());
        mockMvc.perform(get("/application/" + app.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-details"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("currentCompetition", app.getCompetition()));
    }

    @Test
    public void testApplicationSummary() throws Exception {
        Application app = applications.get(0);

        List<Response> reponses = new ArrayList<Response>();

        when(responseService.getResponsesByApplicationId(applications.get(0).getId())).thenReturn(reponses);

        when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
        when(applicationService.getApplicationById(app.getId())).thenReturn(app);

        mockMvc.perform(get("/application/" + app.getId()+"/summary"))
                .andExpect(status().isOk())
                .andExpect(view().name("application-summary"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("currentCompetition", app.getCompetition()));
    }

//    @Test
//    public void testNotExistingApplicationDetails() throws Exception {
//        Application app = applications.get(0);
//
//        when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
//        when(applicationService.getApplicationById(app.getId())).thenReturn(app);
//
//        System.out.println("Show dashboard for application: "+ app.getId());
//        mockMvc.perform(get("/application/1234"))
//                .andExpect(status().isNotFound())
//                .andExpect(view().name("application-details"))
//                .andExpect(model().attribute("currentApplication", app))
//                .andExpect(model().attribute("currentCompetition", app.getCompetition()));
//    }

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