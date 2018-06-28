package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.AbstractApplicationMockMVCTest;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.finance.view.ApplicationFinanceOverviewModelManager;
import org.innovateuk.ifs.application.finance.view.DefaultFinanceModelManager;
import org.innovateuk.ifs.application.forms.populator.InterviewFeedbackViewModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationSectionAndQuestionModelPopulator;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.application.summary.controller.ApplicationInterviewSummaryController;
import org.innovateuk.ifs.application.summary.populator.ApplicationFinanceSummaryViewModelPopulator;
import org.innovateuk.ifs.application.summary.populator.ApplicationFundingBreakdownViewModelPopulator;
import org.innovateuk.ifs.application.summary.populator.ApplicationInterviewSummaryViewModelPopulator;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationInterviewSummaryViewModel;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentFeedbackResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.interview.service.InterviewResponseRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.populator.OrganisationDetailsModelPopulator;
import org.innovateuk.ifs.user.resource.*;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.assessment.builder.ApplicationAssessmentFeedbackResourceBuilder.newApplicationAssessmentFeedbackResource;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationInterviewSummaryControllerTest extends AbstractApplicationMockMVCTest<ApplicationInterviewSummaryController> {

    @Spy
    @InjectMocks
    private ApplicationModelPopulator applicationModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationSectionAndQuestionModelPopulator applicationSectionAndQuestionModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationFinanceSummaryViewModelPopulator applicationFinanceSummaryViewModelPopulator;

    @Spy
    @InjectMocks
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;

    @Mock
    private ApplicationFinanceOverviewModelManager applicationFinanceOverviewModelManager;

    @Spy
    @InjectMocks
    private InterviewFeedbackViewModelPopulator interviewFeedbackViewModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationInterviewSummaryViewModelPopulator applicationInterviewSummaryViewModelPopulator;

    @Mock
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Mock
    private AssessmentRestService assessmentRestService;

    @Mock
    private InterviewAssignmentRestService interviewAssignmentRestService;

    @Mock
    private InterviewResponseRestService interviewResponseRestService;

    @Mock
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private FormInputViewModelGenerator formInputViewModelGenerator;

    @Mock
    private CategoryRestService categoryRestServiceMock;

    @Mock
    private ApplicantRestService applicantRestService;

    @Override
    protected ApplicationInterviewSummaryController supplyControllerUnderTest() {
        return new ApplicationInterviewSummaryController();
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
    public void testApplicationSummary() throws Exception {

        ApplicationResource application = applications.get(0);

        UserResource user = newUserResource()
                .withId(1L)
                .build();

        OrganisationResource organisation = newOrganisationResource()
                .withId(2L)
                .build();

        ProcessRoleResource leadApplicantProcessRole = newProcessRoleResource()
                .withUser(user)
                .withRole(Role.LEADAPPLICANT)
                .withOrganisation(organisation.getId())
                .build();

        ApplicationAssessmentFeedbackResource applicationAssessmentFeedback = newApplicationAssessmentFeedbackResource()
                .build();

        FileEntryResource response = newFileEntryResource()
                .withName("response")
                .build();

        FileEntryResource feedback = newFileEntryResource()
                .withName("feedback")
                .build();

        ApplicationAssessmentAggregateResource applicationAssessmentAggregate = new ApplicationAssessmentAggregateResource();

        ApplicationTeamResource applicationTeam = new ApplicationTeamResource();
        ApplicationTeamOrganisationResource teamOrganisation = new ApplicationTeamOrganisationResource();
        ApplicationTeamUserResource applicationTeamUser = new ApplicationTeamUserResource();

        applicationTeamUser.setLead(true);
        applicationTeamUser.setEmail("kieran@worth.systems");
        teamOrganisation.setUsers(Collections.singletonList(applicationTeamUser));
        applicationTeam.setLeadOrganisation(teamOrganisation);

        when(applicationService.getLeadOrganisation(application.getId())).thenReturn(organisation);
        when(applicationSummaryRestService.getApplicationTeam(application.getId())).thenReturn(restSuccess(applicationTeam));
        when(userRestService.findUserByEmail(applicationTeamUser.getEmail())).thenReturn(restSuccess(user));
        when(userRestService.findProcessRole(user.getId(), application.getId())).thenReturn(restSuccess(leadApplicantProcessRole));
        when(assessorFormInputResponseRestService.getApplicationAssessmentAggregate(application.getId())).thenReturn(restSuccess(applicationAssessmentAggregate));
        when(assessmentRestService.getApplicationFeedback(application.getId())).thenReturn(restSuccess(applicationAssessmentFeedback));
        when(interviewResponseRestService.findResponse(application.getId())).thenReturn(restSuccess(response));
        when(interviewAssignmentRestService.findFeedback(application.getId())).thenReturn(restSuccess(feedback));

        DefaultFinanceModelManager financeManager = mock(DefaultFinanceModelManager.class);
        when(financeViewHandlerProvider.getFinanceModelManager(0)).thenReturn(financeManager);

        MvcResult result = mockMvc.perform(get("/application/" + application.getId() + "/interview-summary"))
                .andExpect(status().isOk())
                .andExpect(view().name("application-interview-summary"))
                .andReturn();

        ApplicationInterviewSummaryViewModel model = (ApplicationInterviewSummaryViewModel) result.getModelAndView().getModel().get("applicationInterviewSummaryViewModel");

        assertEquals(model.getApplication(), application);
        assertEquals(model.getCompetition().getId(), competitionId);
        assertEquals(model.getLeadOrganisation(), organisations.get(0));
        assertEquals(model.getResponses(), formInputsToFormInputResponses);
        assertEquals(model.getFeedback(), applicationAssessmentFeedback.getFeedback());
        assertEquals(model.getScores(), applicationAssessmentAggregate);
    }
}
