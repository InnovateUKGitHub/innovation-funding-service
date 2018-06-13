package org.innovateuk.ifs.application.forms.controller;

import com.google.common.collect.ImmutableMap;
import org.innovateuk.ifs.AbstractApplicationMockMVCTest;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.common.populator.ApplicationFinanceSummaryViewModelPopulator;
import org.innovateuk.ifs.application.common.populator.ApplicationFundingBreakdownViewModelPopulator;
import org.innovateuk.ifs.application.common.populator.ApplicationResearchParticipationViewModelPopulator;
import org.innovateuk.ifs.application.feedback.populator.ApplicationFeedbackSummaryViewModelPopulator;
import org.innovateuk.ifs.application.feedback.populator.ApplicationInterviewFeedbackViewModelPopulator;
import org.innovateuk.ifs.application.finance.view.ApplicationFinanceOverviewModelManager;
import org.innovateuk.ifs.application.feedback.populator.InterviewFeedbackViewModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationSectionAndQuestionModelPopulator;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.summary.controller.ApplicationSummaryController;
import org.innovateuk.ifs.application.summary.populator.*;
import org.innovateuk.ifs.application.feedback.viewmodel.ApplicationFeedbackSummaryViewModel;
import org.innovateuk.ifs.application.feedback.viewmodel.ApplicationInterviewFeedbackViewModel;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationSummaryViewModel;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentFeedbackResource;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.interview.service.InterviewResponseRestService;
import org.innovateuk.ifs.populator.OrganisationDetailsModelPopulator;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.application.service.Futures.settable;
import static org.innovateuk.ifs.assessment.builder.ApplicationAssessmentFeedbackResourceBuilder.newApplicationAssessmentFeedbackResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.ASSESSOR_FEEDBACK;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.PROJECT_SETUP;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationSummaryControllerTest extends AbstractApplicationMockMVCTest<ApplicationSummaryController> {

    @Spy
    @InjectMocks
    private ApplicationModelPopulator applicationModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationInterviewFeedbackViewModelPopulator applicationInterviewFeedbackViewModelPopulator;

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
    private ApplicationFeedbackSummaryViewModelPopulator applicationFeedbackSummaryViewModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationResearchParticipationViewModelPopulator applicationResearchParticipationViewModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationSectionAndQuestionModelPopulator applicationSectionAndQuestionModelPopulator;

    @Spy
    @InjectMocks
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;

    @Mock
    private ApplicationFinanceOverviewModelManager applicationFinanceOverviewModelManager;

    @Spy
    @InjectMocks
    private InterviewFeedbackViewModelPopulator interviewFeedbackViewModelPopulator;

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
    private ProjectService projectService;

    @Mock
    private InterviewResponseRestService interviewResponseRestService;

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
        when(formInputViewModelGenerator.fromQuestion(any(), any())).thenReturn(Collections.emptyList());
        when(organisationService.getOrganisationForUser(anyLong(), anyList())).thenReturn(ofNullable(organisations.get(0)));
        when(categoryRestServiceMock.getResearchCategories()).thenReturn(restSuccess(newResearchCategoryResource().build(2)));
    }

    @Test
    public void testApplicationSummaryWithProjectWithdrawn() throws Exception {
        CompetitionResource competition = competitionResources.get(0);
        competition.setCompetitionStatus(PROJECT_SETUP);

        ApplicationAssessmentAggregateResource aggregateResource = new ApplicationAssessmentAggregateResource(
                true, 5, 4, ImmutableMap.of(1L, new BigDecimal("2")), 3L);

        ApplicationResource app = applications.get(0);
        app.setCompetition(competition.getId());

        when(applicationService.getById(app.getId())).thenReturn(app);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        ProcessRoleResource userApplicationRole = newProcessRoleResource().withApplication(app.getId()).withOrganisation(
                organisations.get(0).getId()).build();
        when(userRestServiceMock.findProcessRole(loggedInUser.getId(), app.getId())).thenReturn(restSuccess(
                userApplicationRole));

        when(assessorFormInputResponseRestService.getApplicationAssessmentAggregate(app.getId()))
                .thenReturn(restSuccess(aggregateResource));

        ApplicationAssessmentFeedbackResource expectedFeedback = newApplicationAssessmentFeedbackResource()
                .withFeedback(asList("Feedback 1", "Feedback 2"))
                .build();

        when(assessmentRestService.getApplicationFeedback(app.getId())).thenReturn(restSuccess(expectedFeedback));

        ProjectResource project = newProjectResource().withProjectState(ProjectState.WITHDRAWN).build();
        when(projectService.getByApplicationId(app.getId())).thenReturn(project);

        when(interviewAssignmentRestService.isAssignedToInterview(app.getId())).thenReturn(restSuccess(false));

        MvcResult result = mockMvc.perform(get("/application/" + app.getId() + "/summary"))
                .andExpect(status().isOk())
                .andExpect(view().name("application-feedback-summary"))
                .andReturn();
    }


    @Test
    public void testApplicationSummary() throws Exception {
        ApplicationResource app = applications.get(0);
        when(applicationService.getById(app.getId())).thenReturn(app);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));
        ProcessRoleResource userApplicationRole = newProcessRoleResource().withApplication(app.getId()).withOrganisation(organisations.get(0).getId()).build();
        when(userRestServiceMock.findProcessRole(loggedInUser.getId(), app.getId())).thenReturn(restSuccess(userApplicationRole));
        when(interviewAssignmentRestService.isAssignedToInterview(app.getId())).thenReturn(restSuccess(false));

        ApplicationAssessmentAggregateResource aggregateResource = new ApplicationAssessmentAggregateResource(
                true, 5, 4, ImmutableMap.of(1L, new BigDecimal("2")), 3L);

        List<AssessmentResource> feedbackSummary = newAssessmentResource().build(1);

        when(assessorFormInputResponseRestService.getApplicationAssessmentAggregate(app.getId())).thenReturn(restSuccess(aggregateResource));
        when(assessmentRestService.getByUserAndApplication(loggedInUser.getId(), app.getId())).thenReturn(restSuccess(feedbackSummary));

        MvcResult result = mockMvc.perform(get("/application/" + app.getId() + "/summary"))
                .andExpect(status().isOk())
                .andExpect(view().name("application-summary"))
                .andReturn();

        ApplicationSummaryViewModel model = (ApplicationSummaryViewModel) result.getModelAndView().getModel().get("applicationSummaryViewModel");

        assertEquals(model.getCurrentApplication(), app);
        assertEquals(model.getCurrentCompetition().getId(), app.getCompetition());
        assertEquals(model.getSummaryViewModel().getFeedbackSummary(), feedbackSummary);
        assertEquals(model.getSummaryViewModel().getResponses(), formInputsToFormInputResponses);
        assertEquals(model.isUserIsLeadApplicant(), true);
    }

    @Test
    public void testApplicationInterviewFeedback() throws Exception {
        CompetitionResource competition = competitionResources.get(0);
        competition.setCompetitionStatus(ASSESSOR_FEEDBACK);

        ApplicationAssessmentAggregateResource aggregateResource = new ApplicationAssessmentAggregateResource(
                true, 5, 4, ImmutableMap.of(1L, new BigDecimal("2")), 3L);

        ApplicationAssessmentFeedbackResource expectedFeedback = newApplicationAssessmentFeedbackResource()
                .withFeedback(asList("Feedback 1", "Feedback 2"))
                .build();

        ApplicationResource app = applications.get(0);
        app.setCompetition(competition.getId());

        FileEntryResource interviewFeedback = newFileEntryResource().withName("interviewFeedback").build();
        FileEntryResource interviewResponse = newFileEntryResource().withName("interviewResponse").build();

        when(interviewAssignmentRestService.isAssignedToInterview(app.getId())).thenReturn(restSuccess(true));
        when(assessorFormInputResponseRestService.getApplicationAssessmentAggregate(app.getId())).thenReturn(restSuccess(aggregateResource));
        when(assessmentRestService.getApplicationFeedback(app.getId())).thenReturn(restSuccess(expectedFeedback));
        when(interviewAssignmentRestService.findFeedback(app.getId())).thenReturn(restSuccess(interviewFeedback));
        when(interviewResponseRestService.findResponse(app.getId())).thenReturn(restSuccess(interviewResponse));

        MvcResult result = mockMvc.perform(get("/application/" + app.getId() + "/summary"))
                .andExpect(status().isOk())
                .andExpect(view().name("application-interview-feedback"))
                .andReturn();

        ApplicationInterviewFeedbackViewModel model = (ApplicationInterviewFeedbackViewModel) result.getModelAndView().getModel().get("interviewFeedbackViewModel");
        assertEquals(model.getCurrentApplication(), app);
        assertEquals(model.getCurrentCompetition().getId(), app.getCompetition());
        assertEquals(model.getFeedback(), expectedFeedback.getFeedback());
        assertEquals(model.getFeedbackFilename(), interviewFeedback.getName());
        assertEquals(model.getResponseFilename(), interviewResponse.getName());
        assertEquals(model.getScores(), aggregateResource);

    }

    @Test
    public void testApplicationSummaryWithProjectSetupStatus() throws Exception {
        CompetitionResource competition = competitionResources.get(0);
        competition.setCompetitionStatus(PROJECT_SETUP);

        ApplicationAssessmentAggregateResource aggregateResource = new ApplicationAssessmentAggregateResource(
                true, 5, 4, ImmutableMap.of(1L, new BigDecimal("2")), 3L);

        ApplicationResource app = applications.get(0);
        app.setCompetition(competition.getId());

        when(applicationService.getById(app.getId())).thenReturn(app);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        ProcessRoleResource userApplicationRole = newProcessRoleResource().withApplication(app.getId()).withOrganisation(organisations.get(0).getId()).build();
        when(userRestServiceMock.findProcessRole(loggedInUser.getId(), app.getId())).thenReturn(restSuccess(userApplicationRole));

        when(assessorFormInputResponseRestService.getApplicationAssessmentAggregate(app.getId()))
                .thenReturn(restSuccess(aggregateResource));

        ApplicationAssessmentFeedbackResource expectedFeedback = newApplicationAssessmentFeedbackResource()
                .withFeedback(asList("Feedback 1", "Feedback 2"))
                .build();

        when(interviewAssignmentRestService.isAssignedToInterview(app.getId())).thenReturn(restSuccess(false));

        when(assessmentRestService.getApplicationFeedback(app.getId())).thenReturn(restSuccess(expectedFeedback));

        MvcResult result = mockMvc.perform(get("/application/" + app.getId() + "/summary"))
                .andExpect(status().isOk())
                .andExpect(view().name("application-feedback-summary"))
                .andReturn();

        ApplicationFeedbackSummaryViewModel model = (ApplicationFeedbackSummaryViewModel) result.getModelAndView().getModel().get("applicationFeedbackSummaryViewModel");

        assertEquals(model.getApplication(), app);
        assertEquals(model.getCompetition().getId(), app.getCompetition());
        assertEquals(model.getScores(), aggregateResource);
        assertEquals(model.getFeedback(), expectedFeedback.getFeedback());
    }

    @Test
    public void testUpload() throws Exception {
        CompetitionResource competition = competitionResources.get(0);
        competition.setCompetitionStatus(ASSESSOR_FEEDBACK);
        ApplicationAssessmentAggregateResource aggregateResource = new ApplicationAssessmentAggregateResource(
                true, 5, 4, ImmutableMap.of(1L, new BigDecimal("2")), 3L);
        ApplicationAssessmentFeedbackResource expectedFeedback = newApplicationAssessmentFeedbackResource()
                .withFeedback(asList("Feedback 1", "Feedback 2"))
                .build();
        ApplicationResource app = applications.get(0);
        app.setCompetition(competition.getId());
        setupMocksForGet(app, aggregateResource, expectedFeedback);

        when(interviewResponseRestService.uploadResponse(app.getId(),"application/pdf", 11, "testFile.pdf", "My content!".getBytes()))
                .thenReturn(restSuccess());

        MockMultipartFile file = new MockMultipartFile("response", "testFile.pdf", "application/pdf", "My content!".getBytes());


        mockMvc.perform(
                fileUpload("/application/" + app.getId() + "/summary")
                        .file(file)
                        .param("uploadResponse", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("application-interview-feedback"));

        verify(interviewResponseRestService).uploadResponse(app.getId(),"application/pdf", 11, "testFile.pdf", "My content!".getBytes());
    }

    @Test
    public void testRemove() throws Exception {
        CompetitionResource competition = competitionResources.get(0);
        competition.setCompetitionStatus(ASSESSOR_FEEDBACK);
        ApplicationAssessmentAggregateResource aggregateResource = new ApplicationAssessmentAggregateResource(
                true, 5, 4, ImmutableMap.of(1L, new BigDecimal("2")), 3L);
        ApplicationAssessmentFeedbackResource expectedFeedback = newApplicationAssessmentFeedbackResource()
                .withFeedback(asList("Feedback 1", "Feedback 2"))
                .build();
        ApplicationResource app = applications.get(0);
        app.setCompetition(competition.getId());
        setupMocksForGet(app, aggregateResource, expectedFeedback);

        when(interviewResponseRestService.deleteResponse(app.getId()))
                .thenReturn(restSuccess());

        mockMvc.perform(
                post("/application/" + app.getId() + "/summary")
                        .param("removeResponse", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("application-interview-feedback"));

        verify(interviewResponseRestService).deleteResponse(app.getId());
    }


    private void setupMocksForGet(ApplicationResource app, ApplicationAssessmentAggregateResource aggregateResource,
                                  ApplicationAssessmentFeedbackResource expectedFeedback) {
        when(applicationService.getById(app.getId())).thenReturn(app);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        ProcessRoleResource userApplicationRole = newProcessRoleResource().withApplication(app.getId()).withOrganisation(organisations.get(0).getId()).withRole(Role.LEADAPPLICANT).build();
        when(userRestServiceMock.findProcessRole(loggedInUser.getId(), app.getId())).thenReturn(restSuccess(userApplicationRole));

        when(assessorFormInputResponseRestService.getApplicationAssessmentAggregate(app.getId()))
                .thenReturn(restSuccess(aggregateResource));

        when(assessmentRestService.getApplicationFeedback(app.getId())).thenReturn(restSuccess(expectedFeedback));

        when(interviewAssignmentRestService.isAssignedToInterview(app.getId())).thenReturn(restSuccess(true));

        when(interviewResponseRestService.findResponse(app.getId())).thenReturn(restSuccess(newFileEntryResource().withName("Name").build()));
        when(interviewAssignmentRestService.findFeedback(app.getId())).thenReturn(restSuccess(newFileEntryResource().withName("Name").build()));
    }
}
