package org.innovateuk.ifs.dashboard.controller;

import org.innovateuk.ifs.AbstractApplicationMockMVCTest;
import org.innovateuk.ifs.dashboard.populator.ApplicantDashboardPopulator;
import org.innovateuk.ifs.dashboard.viewmodel.ApplicantDashboardViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    public void setUp() {
        super.setUp();
        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
    }

    @Test
    public void testDashboard() throws Exception {
        ApplicantDashboardViewModel viewModel = mock(ApplicantDashboardViewModel.class);
        when(populator.populate(loggedInUser.getId(), "?origin=APPLICANT_DASHBOARD")).thenReturn(viewModel);

        mockMvc.perform(get("/applicant/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("applicant-dashboard"))
                .andExpect(model().attribute("model", viewModel));
    }

    /**
     * Leadapplicant
     */
    @Test
    public void testDashboardApplicant() throws Exception {
        setLoggedInUser(applicant);

        ApplicantDashboardViewModel viewModel = mock(ApplicantDashboardViewModel.class);
        when(populator.populate(applicant.getId(), "?origin=APPLICANT_DASHBOARD")).thenReturn(viewModel);


        mockMvc.perform(get("/applicant/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("applicant-dashboard"))
                .andExpect(model().attribute("model", viewModel));
    }

    /**
     * Collaborator
     */
    @Test
    public void testDashboardCollaborator() throws Exception {
        UserResource collabUsers = collaborator;
        setLoggedInUser(collabUsers);

        ApplicantDashboardViewModel viewModel = mock(ApplicantDashboardViewModel.class);
        when(populator.populate(collabUsers.getId(), "?origin=APPLICANT_DASHBOARD")).thenReturn(viewModel);

        mockMvc.perform(get("/applicant/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("applicant-dashboard"))
                .andExpect(model().attribute("model", viewModel));
    }
}
