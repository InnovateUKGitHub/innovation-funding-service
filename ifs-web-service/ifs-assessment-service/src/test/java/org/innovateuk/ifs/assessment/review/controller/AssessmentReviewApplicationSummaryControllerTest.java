package org.innovateuk.ifs.assessment.review.controller;

import org.innovateuk.ifs.AbstractApplicationMockMVCTest;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationSectionAndQuestionModelPopulator;
import org.innovateuk.ifs.application.populator.OrganisationDetailsModelPopulator;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.common.populator.ApplicationFinanceSummaryViewModelPopulator;
import org.innovateuk.ifs.application.common.populator.ApplicationFundingBreakdownViewModelPopulator;
import org.innovateuk.ifs.application.common.populator.ApplicationResearchParticipationViewModelPopulator;
import org.innovateuk.ifs.application.common.populator.SummaryViewModelFragmentPopulator;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.finance.view.ApplicationFinanceOverviewModelManager;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.assessment.resource.AssessmentFundingDecisionOutcomeResource;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.review.populator.AssessmentReviewApplicationSummaryModelPopulator;
import org.innovateuk.ifs.assessment.review.viewmodel.AssessmentReviewApplicationSummaryViewModel;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.invite.InviteService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
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
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.application.service.Futures.settable;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeResourceBuilder.newAssessmentFundingDecisionOutcomeResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.SUBMITTED;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.FUNDERS_PANEL;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_SCORE;
import static org.innovateuk.ifs.form.resource.FormInputType.TEXTAREA;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@RunWith(MockitoJUnitRunner.Silent.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessmentReviewApplicationSummaryControllerTest extends AbstractApplicationMockMVCTest<AssessmentReviewApplicationSummaryController> {

    @Spy
    @InjectMocks
    private AssessmentReviewApplicationSummaryModelPopulator assessmentReviewApplicationSummaryModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationModelPopulator applicationModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationSectionAndQuestionModelPopulator applicationSectionAndQuestionModelPopulator;

    @Spy
    @InjectMocks
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;

    @Spy
    @InjectMocks
    private SummaryViewModelFragmentPopulator summaryViewModelPopulator;

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
    private InviteService inviteService;

    @Mock
    private ApplicationFinanceOverviewModelManager applicationFinanceOverviewModelManager;

    @Override
    protected AssessmentReviewApplicationSummaryController supplyControllerUnderTest() {
        return new AssessmentReviewApplicationSummaryController();
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
        long reviewId = 1L;
        ApplicationResource app = applications.get(0);
        when(applicationService.getById(app.getId())).thenReturn(app);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));
        ProcessRoleResource userApplicationRole = newProcessRoleResource().withApplication(app.getId()).withOrganisation(organisations.get(0).getId()).build();
        when(userRestServiceMock.findProcessRole(loggedInUser.getId(), app.getId())).thenReturn(restSuccess(userApplicationRole));
        when(assessmentRestService.getByUserAndApplication(loggedInUser.getId(), app.getId())).thenReturn(restSuccess(emptyList()));


        MvcResult result = mockMvc.perform(get("/review/{reviewId}/application/{applicationId}", reviewId, app.getId()   ))
                .andExpect(status().isOk())
                .andExpect(view().name("assessor-panel-application-overview"))
                .andReturn();

        AssessmentReviewApplicationSummaryViewModel resultModel = (AssessmentReviewApplicationSummaryViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(resultModel.getSummaryViewModel().getCurrentApplication(), app);
        assertEquals(resultModel.getCompetition(), competitionResource);
        assertEquals(resultModel.getSummaryViewModel().getFeedbackSummary(), emptyList());
    }

    @Test
    public void testAssessorCanViewOwnFeedbackOnApplicationWhenInPanel() throws Exception {
        long reviewId = 1L;
        long questionId = 2L;

        UserResource user = loggedInUser;
        ApplicationResource application = applications.get(0);
        CompetitionResource competition = competitionResource;
        String expectedAssessmentFeedback = "assessment feedback";
        String expectedAssessmentComment = "assessment comment";
        String expectedQuestionFeedback = "feedback";
        String expectedQuestionScore = "10";

        application.setCompetition(competition.getId());

        competition.setHasAssessmentPanel(true);
        competition.setCompetitionStatus(FUNDERS_PANEL);

        List<FormInputResource> formInputs = setupAssessmentFormInputs(questionId, ASSESSOR_SCORE, TEXTAREA);
        long formInputIdScore = formInputs.get(0).getId();
        long formInputIdFeedback = formInputs.get(1).getId();

        OrganisationResource collaboratorOrganisation1 = newOrganisationResource().build();
        OrganisationResource collaboratorOrganisation2 = newOrganisationResource().build();
        OrganisationResource leadOrganisation = newOrganisationResource().withId(1L).build();
        OrganisationResource otherOrganisation = newOrganisationResource().build();

        List<UserResource> otherUsers = newUserResource().build(3);

        List<ProcessRoleResource> processRoles = newProcessRoleResource()
                .withOrganisation(collaboratorOrganisation1.getId(),
                        leadOrganisation.getId(),
                        collaboratorOrganisation2.getId(),
                        otherOrganisation.getId())
                .withApplication(application.getId())
                .withUser(otherUsers.get(0), otherUsers.get(1), otherUsers.get(2), user)
                .withRole(COLLABORATOR, LEADAPPLICANT, COLLABORATOR, ASSESSOR)
                .build(4);

        List<AssessmentResource> assessment = newAssessmentResource()
                .withApplication(application.getId())
                .withApplicationName(application.getName())
                .withActivityState(SUBMITTED)
                .withCompetition(competition.getId())
                .withProcessRole(processRoles.get(3).getId())
                .withFundingDecision(newAssessmentFundingDecisionOutcomeResource()
                        .withFundingConfirmation(true)
                        .withFeedback(expectedAssessmentFeedback)
                        .withComment(expectedAssessmentComment)
                        .build())
                .build(1);

        List<AssessorFormInputResponseResource> responses = newAssessorFormInputResponseResource()
                .with(id(null))
                .withAssessment(assessment.get(0).getId())
                .withFormInput(formInputIdScore, formInputIdFeedback)
                .withValue(expectedQuestionScore, expectedQuestionFeedback)
                .build(2);

        when(processRoleService.findProcessRolesByApplicationId(application.getId())).thenReturn(processRoles);
        when(organisationService.getOrganisationById(otherOrganisation.getId())).thenReturn(otherOrganisation);
        when(assessorFormInputResponseRestService.getAllAssessorFormInputResponsesForPanel(processRoles.get(3).getApplicationId())).thenReturn(restSuccess(responses));
        when(assessmentRestService.getByUserAndApplication(user.getId(), application.getId())).thenReturn(restSuccess(assessment));
        when(formInputRestService.getById(responses.get(0).getFormInput())).thenReturn(restSuccess(formInputs.get(0)));
        when(formInputRestService.getById(responses.get(1).getFormInput())).thenReturn(restSuccess(formInputs.get(1)));

        MvcResult result = mockMvc.perform(get("/review/{reviewId}/application/{applicationId}", reviewId, application.getId()   ))
                .andExpect(status().isOk())
                .andExpect(view().name("assessor-panel-application-overview"))
                .andReturn();

        AssessmentReviewApplicationSummaryViewModel resultModel = (AssessmentReviewApplicationSummaryViewModel) result.getModelAndView().getModel().get("model");

        List<AssessmentResource> resultAssessment = resultModel.getSummaryViewModel().getFeedbackSummary();
        AssessmentFundingDecisionOutcomeResource fundingDecisionOutcome = resultAssessment.get(0).getFundingDecision();
        assertEquals(true, fundingDecisionOutcome.getFundingConfirmation());
        assertEquals(expectedAssessmentFeedback, fundingDecisionOutcome.getFeedback());
        assertEquals(expectedAssessmentComment, fundingDecisionOutcome.getComment());

        List<AssessorFormInputResponseResource> assessorScoreResponse = resultModel.getFeedbackViewModel().getScore();
        assertEquals(expectedQuestionScore, assessorScoreResponse.get(0).getValue());

        List<AssessorFormInputResponseResource> assessorFeedbackResponse = resultModel.getFeedbackViewModel().getFeedback();
        assertEquals(expectedQuestionFeedback, assessorFeedbackResponse.get(0).getValue());
    }

    private List<FormInputResource> setupAssessmentFormInputs(long questionId, FormInputType... formInputTypes) {
        List<FormInputResource> formInputs = stream(formInputTypes).map(formInputType ->
                newFormInputResource()
                        .withType(formInputType)
                        .withQuestion(questionId)
                        .build()
        ).collect(toList());

        formInputs.get(0).setDescription("Question score");
        formInputs.get(1).setDescription("Feedback");

        return formInputs;
    }
}
