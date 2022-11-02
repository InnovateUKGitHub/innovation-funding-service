package org.innovateuk.ifs.dashboard.controller;

import org.innovateuk.ifs.AbstractApplicationMockMVCTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.dashboard.populator.ApplicantDashboardPopulator;
import org.innovateuk.ifs.dashboard.viewmodel.ApplicantDashboardViewModel;
import org.innovateuk.ifs.navigation.PageHistory;
import org.innovateuk.ifs.navigation.PageHistoryService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static java.lang.String.valueOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(MockitoJUnitRunner.Silent.class)
public class ApplicantDashboardControllerTest extends AbstractApplicationMockMVCTest<ApplicantDashboardController> {

    @Override
    protected ApplicantDashboardController supplyControllerUnderTest() {
        return new ApplicantDashboardController();
    }

    @Mock
    private ApplicantDashboardPopulator populator;
    @Mock
    private PageHistoryService pageHistoryService;

    @Before
    public void setUpData() {
        this.setupCompetition(FundingType.GRANT, AssessorFinanceView.OVERVIEW);
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


    /**
     * TODO remove this test case once {@link ApplicantDashboardController#loansToApplicationsOverviewPage(Model, UserResource, HttpServletRequest)}  is removed
     */
    @Deprecated
    @Test
    public void redirectToApplicationOverviewWhenCorrectURLReturns() throws Exception {
        setLoansFeatureToggleAndRedirectionURL("/application/1");

        mockMvc.perform(get("/applicant/dashboard/loansCommunity"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/application/1"));
    }

    /**
     * TODO remove this test case once {@link ApplicantDashboardController#loansToApplicationsOverviewPage(Model, UserResource, HttpServletRequest)}  is removed
     */
    @Deprecated
    @Test
    public void redirectToApplicantDashboardWhenInCorrectURLReturns() throws Exception {
        setLoansFeatureToggleAndRedirectionURL(null);

        mockMvc.perform(get("/applicant/dashboard/loansCommunity"))
                .andExpect(status().isOk())
                .andExpect(view().name("applicant-dashboard"));
    }

    @Test
    public void redirectToApplicationOverviewWithCorrectURLReturnsGeneric() throws Exception {
        setRedirectionURL("/application/1");

        mockMvc.perform(get("/applicant/dashboard/overview"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/application/1"));
    }

    @Test
    public void redirectToApplicationOverviewWithIncorrectURLReturnsGeneric() throws Exception {
        setRedirectionURL(null);

        mockMvc.perform(get("/applicant/dashboard/overview"))
                .andExpect(status().isOk())
                .andExpect(view().name("applicant-dashboard"));
    }

    private void setLoansFeatureToggleAndRedirectionURL(String redirectionURL) {
        setLoggedInUser(applicant);
        ReflectionTestUtils.setField(controller, "isLoanPartBEnabled", true);
        ApplicantDashboardViewModel viewModel = mock(ApplicantDashboardViewModel.class);
        String redirectURL = redirectionURL;
        when(populator.populate(applicant.getId())).thenReturn(viewModel);
        when(pageHistoryService.getApplicationOverviewPage(any())).thenReturn(Optional.of(new PageHistory(redirectURL)));
    }

    private void setRedirectionURL(String redirectionURL) {
        setLoggedInUser(applicant);
        ApplicantDashboardViewModel viewModel = mock(ApplicantDashboardViewModel.class);
        when(populator.populate(applicant.getId())).thenReturn(viewModel);
        when(pageHistoryService.getApplicationOverviewPage(any())).thenReturn(Optional.of(new PageHistory(redirectionURL)));
    }
}
