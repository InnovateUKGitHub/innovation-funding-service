package org.innovateuk.ifs.dashboard;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationStatusResource;
import org.innovateuk.ifs.dashboard.ApplicantController;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(locations = "classpath:application.properties")
public class ApplicantControllerTest extends BaseControllerMockMVCTest<ApplicantController> {

    @Override
    protected ApplicantController supplyControllerUnderTest() {
        return new ApplicantController();
    }

    @Before
    public void setUp() {

        super.setUp();

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
        when(projectService.findByUser(loggedInUser.getId())).thenReturn(serviceSuccess(Collections.singletonList(new ProjectResource())));
        when(applicationService.getById(anyLong())).thenReturn(new ApplicationResource());

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
        when(applicationStatusService.getApplicationStatusById(progressMap.get(0).getApplicationStatus())).thenReturn(restSuccess(new ApplicationStatusResource(1L,"CREATED")));
        when(projectService.findByUser(applicant.getId())).thenReturn(serviceSuccess(Collections.singletonList(new ProjectResource())));
        when(applicationService.getById(anyLong())).thenReturn(new ApplicationResource());

        mockMvc.perform(get("/applicant/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("applicant-dashboard"))
                .andExpect(model().attribute("applicationsInProcess", hasSize(1)))
                .andExpect(model().attribute("applicationsFinished", hasSize(0)))
                .andExpect(model().attribute("applicationsAssigned", hasSize(1)));
    }

    /**
     * Collaborator with one application in progress, with one application with assigned questions
     */
    @Test
    public void testDashboardCollaborator() throws Exception {
        UserResource collabUsers = this.users.get(1);
        this.loginUser(collabUsers);

        List<ApplicationResource> progressMap = applications.subList(0,1);
        when(applicationService.getInProgress(collabUsers.getId())).thenReturn(progressMap);

        when(applicationService.getAssignedQuestionsCount(eq(progressMap.get(0).getId()), anyLong())).thenReturn(2);
        when(processRoleService.findProcessRole(this.users.get(1).getId(), progressMap.get(0).getId())).thenReturn(processRoles.get(0));
        when(applicationStatusService.getApplicationStatusById(progressMap.get(0).getApplicationStatus())).thenReturn(restSuccess(new ApplicationStatusResource(1L,"CREATED")));
        when(projectService.findByUser(collabUsers.getId())).thenReturn(serviceSuccess(Collections.singletonList(new ProjectResource())));
        when(applicationService.getById(anyLong())).thenReturn(new ApplicationResource());

        mockMvc.perform(get("/applicant/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("applicant-dashboard"))
                .andExpect(model().attribute("applicationsInProcess", hasSize(1)))
                .andExpect(model().attribute("applicationsFinished", hasSize(0)))
                .andExpect(model().attribute("applicationsAssigned", hasSize(0)));

    }
}
