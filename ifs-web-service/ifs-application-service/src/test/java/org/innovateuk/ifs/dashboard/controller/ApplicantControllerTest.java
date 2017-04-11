package org.innovateuk.ifs.dashboard.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.dashboard.populator.ApplicantDashboardPopulator;
import org.innovateuk.ifs.dashboard.viewmodel.ApplicantDashboardViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(locations = "classpath:application.properties")
public class ApplicantControllerTest extends BaseControllerMockMVCTest<ApplicantController> {

    @Override
    protected ApplicantController supplyControllerUnderTest() {
        return new ApplicantController();
    }

    @Mock
    private ApplicantDashboardPopulator populator;

    @Before
    public void setUp() {
        super.setUp();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
    }
    
    @Test
    public void testDashboard() throws Exception {
        ApplicantDashboardViewModel viewModel = new ApplicantDashboardViewModel();
        when(populator.populate(any(UserResource.class))).thenReturn(viewModel);

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
        this.loginUser(applicant);

        ApplicantDashboardViewModel viewModel = new ApplicantDashboardViewModel();
        when(populator.populate(applicant)).thenReturn(viewModel);


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
        UserResource collabUsers = this.users.get(1);
        this.loginUser(collabUsers);

        ApplicantDashboardViewModel viewModel = new ApplicantDashboardViewModel();
        when(populator.populate(collabUsers)).thenReturn(viewModel);

        mockMvc.perform(get("/applicant/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("applicant-dashboard"))
                .andExpect(model().attribute("model", viewModel));
    }
}
