package org.innovateuk.ifs.dashboard.controller;

import org.innovateuk.ifs.AbstractApplicationMockMVCTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.dashboard.populator.ApplicantDashboardPopulator;
import org.innovateuk.ifs.dashboard.viewmodel.ApplicantDashboardViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.context.TestPropertySource;

import static java.lang.String.valueOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(locations = "classpath:application.properties")
public class ApplicantDashboardControllerTest extends AbstractApplicationMockMVCTest<ApplicantDashboardController> {

    @Override
    protected ApplicantDashboardController supplyControllerUnderTest() {
        return new ApplicantDashboardController();
    }

    @Mock
    private ApplicantDashboardPopulator populator;

    @Before
    public void setUpData() {
        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
    }

    @Test
    public void dashboard() throws Exception {
        ApplicantDashboardViewModel viewModel = mock(ApplicantDashboardViewModel.class);
        when(populator.populate(loggedInUser.getId())).thenReturn(viewModel);

        mockMvc.perform(get("/applicant/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("applicant-dashboard"))
                .andExpect(model().attribute("model", viewModel));
    }

    @Test
    public void dashboardApplicant() throws Exception {
        setLoggedInUser(applicant);

        ApplicantDashboardViewModel viewModel = mock(ApplicantDashboardViewModel.class);
        when(populator.populate(applicant.getId())).thenReturn(viewModel);

        mockMvc.perform(get("/applicant/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("applicant-dashboard"))
                .andExpect(model().attribute("model", viewModel));
    }

    @Test
    public void dashboardCollaborator() throws Exception {
        UserResource collaborator = this.collaborator;
        setLoggedInUser(collaborator);

        ApplicantDashboardViewModel viewModel = mock(ApplicantDashboardViewModel.class);
        when(populator.populate(collaborator.getId())).thenReturn(viewModel);

        mockMvc.perform(get("/applicant/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("applicant-dashboard"))
                .andExpect(model().attribute("model", viewModel));
    }

    @Test
    public void hideApplication() throws Exception {
        UserResource collaborator = this.collaborator;
        setLoggedInUser(collaborator);
        long applicationId = 1L;
        long userId = 1L;

        when(applicationRestService.hideApplication(applicationId, userId)).thenReturn(RestResult.restSuccess());

        mockMvc.perform(post("/applicant/dashboard")
                .param("hide-application", valueOf(applicationId)))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void deleteApplication() throws Exception {
        setLoggedInUser(applicant);
        long applicationId = 1L;

        when(applicationRestService.deleteApplication(applicationId)).thenReturn(RestResult.restSuccess());

        mockMvc.perform(post("/applicant/dashboard").param("delete-application", valueOf(applicationId)))
                .andExpect(status().is3xxRedirection());
    }
}
