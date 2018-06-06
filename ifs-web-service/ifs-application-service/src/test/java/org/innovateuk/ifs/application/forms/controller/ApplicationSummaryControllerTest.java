package org.innovateuk.ifs.application.forms.controller;

import com.google.common.collect.ImmutableMap;
import org.hamcrest.Matchers;
import org.innovateuk.ifs.AbstractApplicationMockMVCTest;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.finance.view.ApplicationFinanceOverviewModelManager;
import org.innovateuk.ifs.application.forms.populator.InterviewFeedbackViewModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationSectionAndQuestionModelPopulator;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.team.populator.ApplicationTeamModelPopulator;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamViewModel;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentFeedbackResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
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

import java.math.BigDecimal;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.application.service.Futures.settable;
import static org.innovateuk.ifs.assessment.builder.ApplicationAssessmentFeedbackResourceBuilder.newApplicationAssessmentFeedbackResource;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.ASSESSOR_FEEDBACK;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.PROJECT_SETUP;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
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

    @Mock
    private ApplicationTeamModelPopulator applicationTeamModelPopulator;

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

        mockMvc.perform(get("/application/" + app.getId() + "/summary"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("projectWithdrawn", true));
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

        mockMvc.perform(get("/application/" + app.getId() + "/summary"))
                .andExpect(status().isOk())
                .andExpect(view().name("application-summary"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("currentCompetition", competitionService.getById(app.getCompetition())))
                .andExpect(model().attribute("leadOrganisation", organisations.get(0)))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasSize(application1Organisations.size())))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasItem(application1Organisations.get(0))))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasItem(application1Organisations.get(1))))
                .andExpect(model().attribute("responses", formInputsToFormInputResponses))
                .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasSize(0)))
                .andExpect(model().attribute("pendingOrganisationNames", Matchers.hasSize(0)))
                .andExpect(model().attribute("applicationTeamModel", applicationTeamViewModel));

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

        setupMocksForGet(app, aggregateResource, expectedFeedback);

        mockMvc.perform(get("/application/" + app.getId() + "/summary"))
                .andExpect(status().isOk())
                .andExpect(view().name("application-interview-feedback"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("currentCompetition", competitionService.getById(app.getCompetition())))
                .andExpect(model().attribute("leadOrganisation", organisations.get(0)))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasSize(application1Organisations.size())))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasItem(application1Organisations.get(0))))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasItem(application1Organisations.get(1))))
                .andExpect(model().attribute("responses", formInputsToFormInputResponses))
                .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasSize(0)))
                .andExpect(model().attribute("pendingOrganisationNames", Matchers.hasSize(0)))
                .andExpect(model().attribute("feedback", expectedFeedback.getFeedback()))
                .andExpect(model().attribute("scores", aggregateResource))
                .andExpect(model().attribute("interviewFeedbackViewModel", Matchers.notNullValue()));
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

        mockMvc.perform(get("/application/" + app.getId() + "/summary"))
                .andExpect(status().isOk())
                .andExpect(view().name("application-feedback-summary"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("currentCompetition", competitionService.getById(app.getCompetition())))
                .andExpect(model().attribute("leadOrganisation", organisations.get(0)))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasSize(application1Organisations.size())))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasItem(application1Organisations.get(0))))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasItem(application1Organisations.get(1))))
                .andExpect(model().attribute("responses", formInputsToFormInputResponses))
                .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasSize(0)))
                .andExpect(model().attribute("pendingOrganisationNames", Matchers.hasSize(0)))
                .andExpect(model().attribute("feedback", expectedFeedback.getFeedback()))
                .andExpect(model().attribute("scores", aggregateResource));
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

    private ApplicationTeamViewModel setupApplicationTeamViewModel() {
        ApplicationTeamViewModel applicationTeamViewModel = new ApplicationTeamViewModel(1L,
                "Application name",
                emptyList(),
                false,
                false,
                false,
                false,
                false);
        applicationTeamViewModel.setSummary(true);
        return applicationTeamViewModel;
    }
}
