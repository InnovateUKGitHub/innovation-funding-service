package org.innovateuk.ifs.management.competition.inflight.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.competition.inflight.populator.CompetitionInFlightStatsModelPopulator;
import org.innovateuk.ifs.management.competition.inflight.viewmodel.CompetitionInFlightStatsViewModel;
import org.innovateuk.ifs.management.decision.controller.CompetitionManagementEOIDecisionController;
import org.innovateuk.ifs.management.decision.form.DecisionFilterForm;
import org.innovateuk.ifs.management.decision.form.DecisionSelectionCookie;
import org.innovateuk.ifs.management.decision.form.DecisionSelectionForm;
import org.innovateuk.ifs.management.decision.populator.CompetitionManagementApplicationDecisionModelPopulator;
import org.innovateuk.ifs.management.decision.service.ApplicationDecisionService;
import org.innovateuk.ifs.management.decision.viewmodel.ManageFundingApplicationsViewModel;
import org.innovateuk.ifs.util.CompressedCookieService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.ApplicationSummaryResourceBuilder.newApplicationSummaryResource;
import static org.innovateuk.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.FUNDERS_PANEL;
import static org.innovateuk.ifs.util.JsonUtil.getSerializedObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CompetitionManagementEoiDecisionControllerTest extends BaseControllerMockMVCTest<CompetitionManagementEOIDecisionController> {

    public static final Long COMPETITION_ID = 123L;
    public static final String FILTER_STRING = "an application id";

    @InjectMocks
    private CompetitionManagementEOIDecisionController controller;

    @Spy
    @InjectMocks
    private CompetitionManagementApplicationDecisionModelPopulator competitionManagementFundingDecisionModelPopulator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Mock
    private CompressedCookieService cookieUtil;

    @Mock
    private ApplicationDecisionService applicationDecisionService;

    @Mock
    private CompetitionInFlightStatsModelPopulator competitionInFlightStatsModelPopulator;

    @Mock
    private ApplicationRestService applicationRestService;

    private MockMvc mockMvc;

    private final DecisionSelectionCookie cookieWithFilterAndSelectionParameters = createCookieWithFilterAndSelectionParameters();

    @Override
    protected CompetitionManagementEOIDecisionController supplyControllerUnderTest() {
        return new CompetitionManagementEOIDecisionController();
    }

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        ReflectionTestUtils.setField(controller, "validator", validator);

        when(competitionInFlightStatsModelPopulator.populateEoiStatsViewModel(any(CompetitionResource.class)))
                .thenReturn(new CompetitionInFlightStatsViewModel());
    }

    @Test
    public void testGetEoiApplications() throws Exception {
        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        CompetitionResource competitionResource = newCompetitionResource().withId(COMPETITION_ID).build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));
        when(applicationSummaryRestService.getAllSubmittedEoiApplicationIds(COMPETITION_ID, empty(), empty(), empty())).thenReturn(restSuccess(asList(1L, 2L)));

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, expectedSummaries, 1, 20);
        when(applicationSummaryRestService.getSubmittedEoiApplications(COMPETITION_ID, "id", 0, 20, empty(), empty(), empty())).thenReturn(restSuccess(summary));

        Map<String, Object> model = mockMvc.perform(get("/competition/{competitionId}/applications/eoi", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"))
                .andReturn().getModelAndView().getModel();

        ManageFundingApplicationsViewModel viewModel = (ManageFundingApplicationsViewModel) model.get("model");

        assertEquals(viewModel.getCompetitionSummary(), competitionSummaryResource);
        assertEquals(viewModel.getResults(), summary);
        assertTrue(viewModel.isExpressionOfInterestEnabled());

        verify(applicationSummaryRestService).getSubmittedEoiApplications(COMPETITION_ID, "id", 0, 20, empty(), empty(), empty());
        verify(applicationSummaryRestService).getCompetitionSummary(COMPETITION_ID);
    }

    @Test
    public void applications_validSubmitDecisionShouldResultInServiceCall() throws Exception {
        String decision = "EOI_APPROVED";
        List<Long> applicationIds = asList(1L, 2L);

        ApplicationResource applicationResource = newApplicationResource()
                .withApplicationExpressionOfInterestConfigResource(ApplicationExpressionOfInterestConfigResource.builder()
                        .enabledForExpressionOfInterest(true)
                        .build())
                .build();
        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(FUNDERS_PANEL)
                .build();
        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withEnabledForExpressionOfInterest(true)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));
        when(applicationDecisionService.saveApplicationDecisionData(COMPETITION_ID, Decision.EOI_APPROVED, applicationIds)).thenReturn(ServiceResult.serviceSuccess());
        when(applicationDecisionService.getDecisionForString(decision)).thenReturn(Optional.of(Decision.ON_HOLD));
        when(applicationSummaryRestService.getAllSubmittedApplicationIds(COMPETITION_ID, empty(), empty())).thenReturn(restSuccess(applicationIds));
        when(cookieUtil.getCookieValue(any(),any())).thenReturn(getSerializedObject(cookieWithFilterAndSelectionParameters));
        when(applicationRestService.getApplicationById(1L)).thenReturn(restSuccess(applicationResource));
        when(applicationRestService.getApplicationById(2L)).thenReturn(restSuccess(applicationResource));

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, expectedSummaries, 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "id", 0, 20, empty(), empty())).thenReturn(restSuccess(summary));

        Map<String, Object> model = mockMvc.perform(post("/competition/{competitionId}/applications/eoi", COMPETITION_ID)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("decision", decision))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"))
                .andReturn().getModelAndView().getModel();

        ManageFundingApplicationsViewModel viewModel = (ManageFundingApplicationsViewModel) model.get("model");

        assertEquals(viewModel.getCompetitionSummary(), competitionSummaryResource);
        assertEquals(viewModel.getResults(), summary);

        verify(cookieUtil).getCookieValue(any(),any());
    }

    private DecisionSelectionCookie createCookieWithFilterAndSelectionParameters() {
        DecisionSelectionCookie cookieWithFilterAndSelectionParameters = new DecisionSelectionCookie();

        DecisionFilterForm filterForm  = new DecisionFilterForm();
        filterForm.setStringFilter(Optional.of(FILTER_STRING));

        DecisionSelectionForm decisionSelectionForm = new DecisionSelectionForm();
        decisionSelectionForm.setAllSelected(true);
        decisionSelectionForm.setApplicationIds(asList(1L, 2L));

        cookieWithFilterAndSelectionParameters.setDecisionSelectionForm(decisionSelectionForm);
        cookieWithFilterAndSelectionParameters.setDecisionFilterForm(filterForm);

        return cookieWithFilterAndSelectionParameters;
    }
}
