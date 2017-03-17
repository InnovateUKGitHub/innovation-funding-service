package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.service.ApplicationFundingDecisionService;
import org.innovateuk.ifs.application.service.ApplicationSummaryService;
import org.innovateuk.ifs.application.service.AssessorFeedbackService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.ApplicationSummarySortFieldService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationSummaryResourceBuilder.newApplicationSummaryResource;
import static org.innovateuk.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.FUNDERS_PANEL;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionManagementFundingControllerTest {

    public static final Long COMPETITION_ID = Long.valueOf(123L);

    @InjectMocks
    private CompetitionManagementFundingController controller;

    @Mock
    private ApplicationSummaryService applicationSummaryService;

    @Mock
    private ApplicationSummarySortFieldService applicationSummarySortFieldService;

    @Mock
    private AssessorFeedbackService assessorFeedbackService;

    @Mock
    private CompetitionService competitionService;

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
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("sortfield");

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50,3, expectedSummaries, 1, 20);
        when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20, "", Optional.empty())).thenReturn(summary);

        mockMvc.perform(get("/competition/{competitionId}/funding", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", summary))
                .andExpect(model().attribute("activeSortField", "sortfield"));

        verify(applicationSummaryService).getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20, "", Optional.empty());
        verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }

    @Test
    public void applications_validSubmitFundingDecisionShouldResultInServiceCall() throws Exception {
        String fundingDecision = "ON_HOLD";
        List<Long> applicationIds = new ArrayList<>();
        applicationIds.add(8L);
        applicationIds.add(9L);
        applicationIds.add(10L);

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);
        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("sortfield");
        when(applicationFundingDecisionService.saveApplicationFundingDecisionData(COMPETITION_ID, FundingDecision.ON_HOLD, applicationIds)).thenReturn(ServiceResult.serviceSuccess());
        when(applicationFundingDecisionService.getFundingDecisionForString(fundingDecision)).thenReturn(Optional.of(FundingDecision.ON_HOLD));

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50,3, expectedSummaries, 1, 20);
        when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20, "", Optional.empty())).thenReturn(summary);

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
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);
        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("sortfield");
        when(applicationFundingDecisionService.saveApplicationFundingDecisionData(COMPETITION_ID, FundingDecision.ON_HOLD, applicationIds)).thenReturn(ServiceResult.serviceSuccess());
        when(applicationFundingDecisionService.getFundingDecisionForString(fundingDecision)).thenReturn(Optional.of(FundingDecision.ON_HOLD));

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50,3, expectedSummaries, 1, 20);
        when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20, "", Optional.empty())).thenReturn(summary);

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
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);
        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("sortfield");
        when(applicationFundingDecisionService.saveApplicationFundingDecisionData(COMPETITION_ID, FundingDecision.ON_HOLD, applicationIds)).thenReturn(ServiceResult.serviceSuccess());
        when(applicationFundingDecisionService.getFundingDecisionForString(fundingDecision)).thenReturn(Optional.of(FundingDecision.ON_HOLD));

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50,3, expectedSummaries, 1, 20);
        when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20, "", Optional.empty())).thenReturn(summary);

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
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);
        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("sortfield");
        when(applicationFundingDecisionService.saveApplicationFundingDecisionData(COMPETITION_ID, FundingDecision.ON_HOLD, applicationIds)).thenReturn(ServiceResult.serviceSuccess());
        when(applicationFundingDecisionService.getFundingDecisionForString(fundingDecisionString)).thenReturn(Optional.empty());

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50,3, expectedSummaries, 1, 20);
        when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20, "", Optional.empty())).thenReturn(summary);

        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("applicationIds", "8")
                .param("applicationIds", "9")
                .param("applicationIds", "10")
                .param("fundingDecision",fundingDecisionString))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"));

        verify(applicationFundingDecisionService, times(0)).saveApplicationFundingDecisionData(any(), any(), any());
    }

    public void setup() {

        ApplicationSummaryPageResource applicationSummaries = new ApplicationSummaryPageResource();
        ApplicationSummaryResource app8 = app(8L);
        ApplicationSummaryResource app9 = app(9L);
        ApplicationSummaryResource app10 = app(10L);
        applicationSummaries.setContent(asList(app8, app9, app10));
        when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(123L, null, 0, Integer.MAX_VALUE, "", Optional.empty())).thenReturn(applicationSummaries);
    }

    private ApplicationSummaryResource app(Long id) {
        ApplicationSummaryResource app = new ApplicationSummaryResource();
        app.setId(id);
        return app;
    }
}
