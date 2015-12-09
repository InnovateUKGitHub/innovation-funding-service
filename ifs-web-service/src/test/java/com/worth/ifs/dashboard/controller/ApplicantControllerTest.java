package com.worth.ifs.dashboard.controller;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.dashboard.ApplicantController;
import com.worth.ifs.user.domain.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


//@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
//@ContextConfiguration
public class ApplicantControllerTest extends BaseUnitTest {


    @InjectMocks
    private ApplicantController applicantController;



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
        this.setupApplicationResponses();
        this.loginDefaultUser();



    }




    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testDashboard() throws Exception {

        mockMvc.perform(get("/applicant/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("applicant-dashboard"))
                .andExpect(model().attribute("applicationsInProcess", hasSize(0)))
                .andExpect(model().attribute("applicationsFinished", hasSize(0)))
                .andExpect(model().attribute("applicationsAssigned", hasSize(0)));

    }

    /**
     * leadapplicant with one application in progress, assigned is not displayed for leadapplicant
     */
    @Test
    public void testDashboardApplicant() throws Exception {
        this.loginUser(applicant);

        List<ApplicationResource> progressMap = applications.subList(0,1);
        when(applicationService.getInProgress(applicant.getId())).thenReturn(progressMap);
        when(applicationService.getAssignedQuestionsCount(eq(progressMap.get(0).getId()), anyLong())).thenReturn(2);

        mockMvc.perform(get("/applicant/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("applicant-dashboard"))
                .andExpect(model().attribute("applicationsInProcess", hasSize(1)))
                .andExpect(model().attribute("applicationsFinished", hasSize(0)))
                .andExpect(model().attribute("applicationsAssigned", hasSize(0)));

    }

    /**
     * Collaborator with one application in progress, with one application with assigned questions
     */
    @Test
    public void testDashboardCollaborator() throws Exception {
        User collabUsers = this.users.get(1);
        this.loginUser(collabUsers);

        List<ApplicationResource> progressMap = applications.subList(0,1);
        when(applicationService.getInProgress(collabUsers.getId())).thenReturn(progressMap);

        when(applicationService.getAssignedQuestionsCount(eq(progressMap.get(0).getId()), anyLong())).thenReturn(2);

        mockMvc.perform(get("/applicant/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("applicant-dashboard"))
                .andExpect(model().attribute("applicationsInProcess", hasSize(1)))
                .andExpect(model().attribute("applicationsFinished", hasSize(0)))
                .andExpect(model().attribute("applicationsAssigned", hasSize(1)));

    }
}