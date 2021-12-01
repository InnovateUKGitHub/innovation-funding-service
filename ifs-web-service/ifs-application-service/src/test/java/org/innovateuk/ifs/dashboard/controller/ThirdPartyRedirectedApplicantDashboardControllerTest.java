package org.innovateuk.ifs.dashboard.controller;

import org.innovateuk.ifs.AbstractApplicationMockMVCTest;
import org.innovateuk.ifs.application.overview.populator.ApplicationOverviewModelPopulator;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.dashboard.populator.ApplicantDashboardPopulator;
import org.innovateuk.ifs.dashboard.viewmodel.ApplicantDashboardViewModel;
import org.innovateuk.ifs.navigation.PageHistory;
import org.innovateuk.ifs.navigation.PageHistoryService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static java.lang.String.valueOf;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(locations = {"classpath:application.properties", "classpath:/application-web-core.properties"})
public class ThirdPartyRedirectedApplicantDashboardControllerTest extends AbstractApplicationMockMVCTest<ApplicantDashboardController> {

    @Override
    protected ThirdPartyRedirectedApplicantDashboardController supplyControllerUnderTest() {
        return new ThirdPartyRedirectedApplicantDashboardController();
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
    public void canGetApplicationOverviewRedirectionURL() throws Exception {
        setLoggedInUser(applicant);
        ReflectionTestUtils.setField(controller, "isLoanPartBEnabled", true);
        ApplicantDashboardViewModel viewModel = mock(ApplicantDashboardViewModel.class);
        String redirectURL = "/application/1";
        when(populator.populate(applicant.getId())).thenReturn(viewModel);
        when(pageHistoryService.getPreviousPage(any())).thenReturn(Optional.of(new PageHistory(redirectURL)));

        mockMvc.perform(get("/applicant/dashboard/loansCommunity"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/application/1"));
    }

    @Test
    public void cannotGetApplicationOverviewRedirectionURL() throws Exception {
        setLoggedInUser(applicant);
        ReflectionTestUtils.setField(controller, "isLoanPartBEnabled", true);
        ApplicantDashboardViewModel viewModel = mock(ApplicantDashboardViewModel.class);
        String redirectURL = null;
        when(populator.populate(applicant.getId())).thenReturn(viewModel);
        when(pageHistoryService.getPreviousPage(any())).thenReturn(Optional.of(new PageHistory(null)));

        mockMvc.perform(get("/applicant/dashboard/loansCommunity"))
                .andExpect(status().isOk())
                .andExpect(view().name("applicant-dashboard"));
    }
}
