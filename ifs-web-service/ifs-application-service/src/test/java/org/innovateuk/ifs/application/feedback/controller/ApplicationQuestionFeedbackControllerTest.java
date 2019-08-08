package org.innovateuk.ifs.application.feedback.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.feedback.populator.AssessorQuestionFeedbackPopulator;
import org.innovateuk.ifs.application.feedback.populator.FeedbackNavigationPopulator;
import org.innovateuk.ifs.application.feedback.viewmodel.AssessQuestionFeedbackViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentFeedbackAggregateResourceBuilder.newAssessmentFeedbackAggregateResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.ASSESSOR_FEEDBACK;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.PROJECT_SETUP;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApplicationQuestionFeedbackControllerTest extends BaseControllerMockMVCTest<ApplicationQuestionFeedbackController> {

    @Spy
    @InjectMocks
    private AssessorQuestionFeedbackPopulator assessorQuestionFeedbackPopulator;

    @Spy
    @InjectMocks
    private FeedbackNavigationPopulator feedbackNavigationPopulator;

    @Mock
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Mock
    private InterviewAssignmentRestService interviewAssignmentRestService;

    @Mock
    private FormInputResponseRestService formInputResponseRestService;

    @Mock
    private QuestionService questionService;

    @Mock
    private QuestionRestService questionRestService;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private FormInputRestService formInputRestService;

    @Test
    public void applicationAssessorQuestionFeedback() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;

        QuestionResource previousQuestion = newQuestionResource().withId(1L).withShortName("previous").build();
        QuestionResource questionResource = newQuestionResource().withId(questionId).build();
        QuestionResource nextQuestion = newQuestionResource().withId(3L).withShortName("next").build();
        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).withCompetitionStatus(PROJECT_SETUP).build();
        List<FormInputResponseResource> responseResources = newFormInputResponseResource().build(2);
        List<FormInputResource> formInputs = newFormInputResource().build(2);
        AssessmentFeedbackAggregateResource aggregateResource = newAssessmentFeedbackAggregateResource().build();
        NavigationViewModel expectedNavigation = new NavigationViewModel();
        expectedNavigation.setNextText("next");
        expectedNavigation.setNextUrl("/application/1/question/3/feedback");
        expectedNavigation.setPreviousText("previous");
        expectedNavigation.setPreviousUrl("/application/1/question/1/feedback");


        AssessQuestionFeedbackViewModel expectedModel =
                new AssessQuestionFeedbackViewModel(applicationResource, questionResource, responseResources, formInputs, aggregateResource, expectedNavigation);

        when(questionService.getPreviousQuestion(questionId)).thenReturn(Optional.ofNullable(previousQuestion));
        when(questionRestService.findById(questionId)).thenReturn(restSuccess(questionResource));
        when(questionService.getNextQuestion(questionId)).thenReturn(Optional.ofNullable(nextQuestion));
        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(applicationResource));
        when(formInputRestService.getByQuestionId(questionId)).thenReturn(restSuccess(formInputs));
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
