package org.innovateuk.ifs.application.feedback.controller;

import org.innovateuk.ifs.AbstractApplicationMockMVCTest;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.feedback.populator.AssessorQuestionFeedbackPopulator;
import org.innovateuk.ifs.application.feedback.populator.FeedbackNavigationPopulator;
import org.innovateuk.ifs.application.feedback.viewmodel.AssessQuestionFeedbackViewModel;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.interview.service.InterviewResponseRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentFeedbackAggregateResourceBuilder.newAssessmentFeedbackAggregateResource;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.ASSESSOR_FEEDBACK;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.PROJECT_SETUP;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApplicationQuestionFeedbackControllerTest extends AbstractApplicationMockMVCTest<ApplicationQuestionFeedbackController> {

    @Spy
    @InjectMocks
    private AssessorQuestionFeedbackPopulator assessorQuestionFeedbackPopulator;

    @Spy
    @InjectMocks
    private FeedbackNavigationPopulator feedbackNavigationPopulator;

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
    private InterviewResponseRestService interviewResponseRestService;

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
    public void applicationAssessorQuestionFeedback() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;

        QuestionResource previousQuestion = newQuestionResource().withId(1L).withShortName("previous").build();
        QuestionResource questionResource = newQuestionResource().withId(questionId).build();
        QuestionResource nextQuestion = newQuestionResource().withId(3L).withShortName("next").build();
        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).withCompetitionStatus(PROJECT_SETUP).build();
        List<FormInputResponseResource> responseResources = newFormInputResponseResource().build(2);
        AssessmentFeedbackAggregateResource aggregateResource = newAssessmentFeedbackAggregateResource().build();
        NavigationViewModel expectedNavigation = new NavigationViewModel();
        expectedNavigation.setNextText("next");
        expectedNavigation.setNextUrl("/application/1/question/3/feedback");
        expectedNavigation.setPreviousText("previous");
        expectedNavigation.setPreviousUrl("/application/1/question/1/feedback");
        AssessQuestionFeedbackViewModel expectedModel =
                new AssessQuestionFeedbackViewModel(applicationResource, questionResource, responseResources, aggregateResource, expectedNavigation);

        when(questionService.getPreviousQuestion(questionId)).thenReturn(Optional.ofNullable(previousQuestion));
        when(questionService.getById(questionId)).thenReturn(questionResource);
        when(questionService.getNextQuestion(questionId)).thenReturn(Optional.ofNullable(nextQuestion));
        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(applicationResource));
        when(formInputResponseRestService.getByApplicationIdAndQuestionId(applicationId, questionId)).thenReturn(restSuccess(responseResources));
        when(interviewAssignmentRestService.isAssignedToInterview(applicationId)).thenReturn(restSuccess(false));
        when(assessorFormInputResponseRestService.getAssessmentAggregateFeedback(applicationId, questionId))
                .thenReturn(restSuccess(aggregateResource));

        mockMvc.perform(get("/application/{applicationId}/question/{questionId}/feedback", applicationId, questionId))
                .andExpect(status().isOk())
                .andExpect(view().name("application-assessor-feedback"))
                .andExpect(model().attribute("model", expectedModel));
    }

    @Test
    public void applicationAssessorQuestionFeedback_invalidState() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;

        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).withCompetitionStatus(ASSESSOR_FEEDBACK).build();

        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(applicationResource));
        when(interviewAssignmentRestService.isAssignedToInterview(applicationId)).thenReturn(restSuccess(false));

        mockMvc.perform(get("/application/{applicationId}/question/{questionId}/feedback", applicationId, questionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/application/" + applicationId + "/summary"));
    }

    @Override
    protected ApplicationQuestionFeedbackController supplyControllerUnderTest() {
        return new ApplicationQuestionFeedbackController();
    }
}
