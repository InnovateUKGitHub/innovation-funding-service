package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.service.ApplicationFundingDecisionService;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.ApplicationSummarySortFieldService;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static org.innovateuk.ifs.application.builder.ApplicationSummaryResourceBuilder.newApplicationSummaryResource;
import static org.innovateuk.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.FUNDERS_PANEL;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionManagementFundingControllerTest {

    public static final Long COMPETITION_ID = 123L;

    @InjectMocks
    private CompetitionManagementFundingController controller;

    @Mock
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Mock
    private ApplicationSummarySortFieldService applicationSummarySortFieldService;

    @Mock
    private ApplicationFundingDecisionService applicationFundingDecisionService;

    private MockMvc mockMvc;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        ReflectionTestUtils.setField(controller, "validator", validator);
    }

    @Test
    public void getByCompetitionIdForCompetitionFundersPanelSubmittedRequested() throws Exception {
        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));

        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("sortfield");

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, expectedSummaries, 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "sortfield", 0, 20, "", empty())).thenReturn(restSuccess(summary));

        mockMvc.perform(get("/competition/{competitionId}/funding", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", summary))
                .andExpect(model().attribute("activeSortField", "sortfield"));

        verify(applicationSummaryRestService).getSubmittedApplications(COMPETITION_ID, "sortfield", 0, 20, "", empty());
        verify(applicationSummaryRestService).getCompetitionSummary(COMPETITION_ID);
    }

    @Test
    public void applications_validSubmitFundingDecisionShouldResultInServiceCall() throws Exception {
        String fundingDecision = "ON_HOLD";
        List<Long> applicationIds = new ArrayList<>();
        applicationIds.add(8L);
        applicationIds.add(9L);
        applicationIds.add(10L);

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));
        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("sortfield");
        when(applicationFundingDecisionService.saveApplicationFundingDecisionData(COMPETITION_ID, FundingDecision.ON_HOLD, applicationIds)).thenReturn(ServiceResult.serviceSuccess());
        when(applicationFundingDecisionService.getFundingDecisionForString(fundingDecision)).thenReturn(Optional.of(FundingDecision.ON_HOLD));

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, expectedSummaries, 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "sortfield", 0, 20, "", empty())).thenReturn(restSuccess(summary));

        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicationIds", "8")
                .param("applicationIds", "9")
                .param("applicationIds", "10")
                .param("fundingDecision", fundingDecision))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"));

        verify(applicationFundingDecisionService).saveApplicationFundingDecisionData(COMPETITION_ID, FundingDecision.ON_HOLD, applicationIds);
    }

    @Test
    public void applications_invalidSubmitFundingDecisionShouldNotResultInServiceCall() throws Exception {
        String fundingDecision = "ON_HOLD";
        List<Long> applicationIds = new ArrayList<>();
        applicationIds.add(8L);
        applicationIds.add(9L);
        applicationIds.add(10L);

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));
        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("sortfield");
        when(applicationFundingDecisionService.saveApplicationFundingDecisionData(COMPETITION_ID, FundingDecision.ON_HOLD, applicationIds)).thenReturn(ServiceResult.serviceSuccess());
        when(applicationFundingDecisionService.getFundingDecisionForString(fundingDecision)).thenReturn(Optional.of(FundingDecision.ON_HOLD));

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, expectedSummaries, 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "sortfield", 0, 20, "", empty())).thenReturn(restSuccess(summary));

        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicationIds", "8")
                .param("applicationIds", "9")
                .param("applicationIds", "10")
                .param("fundingDecision", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"));

        verifyNoMoreInteractions(applicationFundingDecisionService);
    }

    @Test
    public void applications_missingSubmitFundingDecisionShouldNotResultInServiceCall() throws Exception {
        String fundingDecision = "ON_HOLD";
        List<Long> applicationIds = new ArrayList<>();
        applicationIds.add(8L);
        applicationIds.add(9L);
        applicationIds.add(10L);

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));
        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("sortfield");
        when(applicationFundingDecisionService.saveApplicationFundingDecisionData(COMPETITION_ID, FundingDecision.ON_HOLD, applicationIds)).thenReturn(ServiceResult.serviceSuccess());
        when(applicationFundingDecisionService.getFundingDecisionForString(fundingDecision)).thenReturn(Optional.of(FundingDecision.ON_HOLD));

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, expectedSummaries, 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "sortfield", 0, 20, "", empty())).thenReturn(restSuccess(summary));

        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicationIds", "8")
                .param("applicationIds", "9")
                .param("applicationIds", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"));

        verifyNoMoreInteractions(applicationFundingDecisionService);
    }

    @Test
    public void applications_unlistedFundingChoiceStringShouldNotResultInServiceCall() throws Exception {
        String fundingDecisionString = "abc";
        List<Long> applicationIds = new ArrayList<>();
        applicationIds.add(8L);
        applicationIds.add(9L);
        applicationIds.add(10L);

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));
        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("sortfield");
        when(applicationFundingDecisionService.saveApplicationFundingDecisionData(COMPETITION_ID, FundingDecision.ON_HOLD, applicationIds)).thenReturn(ServiceResult.serviceSuccess());
        when(applicationFundingDecisionService.getFundingDecisionForString(fundingDecisionString)).thenReturn(empty());

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, expectedSummaries, 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "sortfield", 0, 20, "", empty())).thenReturn(restSuccess(summary));

        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicationIds", "8")
                .param("applicationIds", "9")
                .param("applicationIds", "10")
                .param("fundingDecision", fundingDecisionString))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"));

        verify(applicationFundingDecisionService, times(0)).saveApplicationFundingDecisionData(any(), any(), any());
    }

    @Test
    public void applications_filteredParameterNameShouldNotBeReflectedInPaginationViewModel() throws Exception {
        String fundingDecisionString = "abc";
        List<Long> applicationIds = new ArrayList<>();
        applicationIds.add(8L);
        applicationIds.add(9L);
        applicationIds.add(10L);

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));
        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("sortfield");
        when(applicationFundingDecisionService.saveApplicationFundingDecisionData(COMPETITION_ID, FundingDecision.ON_HOLD, applicationIds)).thenReturn(ServiceResult.serviceSuccess());
        when(applicationFundingDecisionService.getFundingDecisionForString(fundingDecisionString)).thenReturn(empty());

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, expectedSummaries, 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "sortfield", 0, 20, "", empty())).thenReturn(restSuccess(summary));

        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicationIds", "8")
                .param("applicationIds", "9")
                .param("applicationIds", "10")
                .param("fundingDecision", fundingDecisionString)
                .param("_csrf", "hash")
        )
                .andExpect(status().isOk()).andReturn();

        PaginationViewModel paginationViewModel = (PaginationViewModel) result.getModelAndView().getModel().get("pagination");

        assertEquals("?origin=FUNDING_APPLICATIONS&page=0",paginationViewModel.getPageNames().get(0).getPath());

    }

    private ApplicationSummaryResource app(Long id) {
        ApplicationSummaryResource app = new ApplicationSummaryResource();
        app.setId(id);
        return app;
    }
}
