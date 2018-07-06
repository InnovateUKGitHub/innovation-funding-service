package org.innovateuk.ifs.application.forms.controller;

import com.google.common.collect.ImmutableMap;
import org.innovateuk.ifs.AbstractApplicationMockMVCTest;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.forms.researchcategory.populator.ApplicationResearchCategorySummaryModelPopulator;
import org.innovateuk.ifs.application.forms.researchcategory.viewmodel.ResearchCategorySummaryViewModel;
import org.innovateuk.ifs.application.common.populator.ApplicationFinanceSummaryViewModelPopulator;
import org.innovateuk.ifs.application.common.populator.ApplicationFundingBreakdownViewModelPopulator;
import org.innovateuk.ifs.application.common.populator.ApplicationResearchParticipationViewModelPopulator;
import org.innovateuk.ifs.application.common.populator.SummaryViewModelPopulator;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.summary.controller.ApplicationSummaryController;
import org.innovateuk.ifs.application.summary.populator.ApplicationSummaryViewModelPopulator;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationSummaryViewModel;
import org.innovateuk.ifs.application.team.populator.ApplicationTeamModelPopulator;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamViewModel;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.application.service.Futures.settable;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.PROJECT_SETUP;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationSummaryControllerTest extends AbstractApplicationMockMVCTest<ApplicationSummaryController> {

    @Spy
    @InjectMocks
    private ApplicationSummaryViewModelPopulator applicationSummaryViewModelPopulator;

    @Spy
    @InjectMocks
    private SummaryViewModelPopulator summaryViewModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationFinanceSummaryViewModelPopulator applicationFinanceSummaryViewModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationResearchParticipationViewModelPopulator applicationResearchParticipationViewModelPopulator;

    @Mock
    private ApplicationTeamModelPopulator applicationTeamModelPopulator;

    @Mock
    private ApplicantRestService applicantRestService;

    @Mock
    private FormInputViewModelGenerator formInputViewModelGenerator;

    @Mock
    private CategoryRestService categoryRestServiceMock;

    @Mock
    private UserRestService userRestServiceMock;

    @Mock
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Mock
    private AssessmentRestService assessmentRestService;

    @Mock
    private InterviewAssignmentRestService interviewAssignmentRestService;

    @Mock
    private ApplicationResearchCategorySummaryModelPopulator researchCategorySummaryModelPopulator;

    @Mock
    private ProjectService projectService;

    @Override
    protected ApplicationSummaryController supplyControllerUnderTest() {
        return new ApplicationSummaryController();
    }

    @Before
    public void setUp() {
        super.setUp();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.setupFinances();
        this.setupInvites();

        questionResources.forEach((id, questionResource) -> when(applicantRestService.getQuestion(any(), any(), eq(questionResource.getId()))).thenReturn(newApplicantQuestionResource().build()));
        when(formInputViewModelGenerator.fromQuestion(any(), any())).thenReturn(emptyList());
        when(organisationService.getOrganisationForUser(anyLong(), anyList())).thenReturn(ofNullable(organisations.get(0)));
        when(categoryRestServiceMock.getResearchCategories()).thenReturn(restSuccess(newResearchCategoryResource().build(2)));
    }

    @Test
    public void testApplicationSummaryWithProjectWithdrawn() throws Exception {
        CompetitionResource competition = competitionResources.get(0);
        competition.setCompetitionStatus(PROJECT_SETUP);

        ApplicationResource app = applications.get(0);
        app.setCompetition(competition.getId());

        when(applicationService.getById(app.getId())).thenReturn(app);
        when(interviewAssignmentRestService.isAssignedToInterview(app.getId())).thenReturn(restSuccess(false));

        mockMvc.perform(get("/application/{applicationId}/summary", app.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/application/" + app.getId() + "/feedback"));
    }

    @Test
    public void testApplicationSummary() throws Exception {
        ApplicationResource app = applications.get(0);
        when(applicationService.getById(app.getId())).thenReturn(app);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));
        ProcessRoleResource userApplicationRole = newProcessRoleResource().withApplication(app.getId()).withOrganisation(organisations.get(0).getId()).build();
        when(userRestServiceMock.findProcessRole(loggedInUser.getId(), app.getId())).thenReturn(restSuccess(userApplicationRole));
        when(interviewAssignmentRestService.isAssignedToInterview(app.getId())).thenReturn(restSuccess(false));
        ApplicationTeamViewModel applicationTeamViewModel = setupApplicationTeamViewModel();
        when(applicationTeamModelPopulator.populateSummaryModel(app.getId(), loggedInUser.getId(), competitionId)).thenReturn
                (applicationTeamViewModel);
        ResearchCategorySummaryViewModel researchCategorySummaryViewModel = setupResearchCategorySummaryViewModel();
        when(researchCategorySummaryModelPopulator.populate(app, loggedInUser.getId(), true)).thenReturn
                (researchCategorySummaryViewModel);

        ApplicationAssessmentAggregateResource aggregateResource = new ApplicationAssessmentAggregateResource(
                true, 5, 4, ImmutableMap.of(1L, new BigDecimal("2")), 3L);

        List<AssessmentResource> feedbackSummary = newAssessmentResource().build(1);

        when(assessorFormInputResponseRestService.getApplicationAssessmentAggregate(app.getId())).thenReturn(restSuccess(aggregateResource));
        when(assessmentRestService.getByUserAndApplication(loggedInUser.getId(), app.getId())).thenReturn(restSuccess(feedbackSummary));

        MvcResult result = mockMvc.perform(get("/application/" + app.getId() + "/summary"))
                .andExpect(status().isOk())
                .andExpect(view().name("application-summary"))
                .andReturn();

        ApplicationSummaryViewModel model = (ApplicationSummaryViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(app, model.getCurrentApplication());
        assertEquals(app.getCompetition(), model.getCurrentCompetition().getId());
        assertEquals(feedbackSummary, model.getSummaryViewModel().getFeedbackSummary());
        assertEquals(formInputsToFormInputResponses, model.getSummaryViewModel().getResponses());
        assertEquals(applicationTeamViewModel, model.getApplicationTeamViewModel());
        assertEquals(researchCategorySummaryViewModel, model.getResearchCategorySummaryViewModel());
        assertTrue(model.isUserIsLeadApplicant());
    }

    private ApplicationTeamViewModel setupApplicationTeamViewModel() {
        ApplicationTeamViewModel applicationTeamViewModel = new ApplicationTeamViewModel(
                1L,
                1L,
                "Application name",
                emptyList(),
                false,
                false,
                false,
                false,
                false,
                false);
        applicationTeamViewModel.setSummary(true);

        return applicationTeamViewModel;
    }

    private ResearchCategorySummaryViewModel setupResearchCategorySummaryViewModel() {
        return new ResearchCategorySummaryViewModel(1L,
                1L,
                "Research category",
                false,
                false,
                false,
                false);
    }
}
