package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.assessment.form.AssessmentSummaryForm;
import com.worth.ifs.assessment.model.AssessmentSummaryModelPopulator;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.assessment.service.AssessorFormInputResponseService;
import com.worth.ifs.assessment.viewmodel.AssessmentSummaryQuestionViewModel;
import com.worth.ifs.assessment.viewmodel.AssessmentSummaryViewModel;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import java.util.List;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static com.worth.ifs.assessment.builder.ProcessOutcomeResourceBuilder.newProcessOutcomeResource;
import static com.worth.ifs.assessment.resource.AssessorFormInputType.*;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.nCopies;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessmentSummaryControllerTest extends BaseControllerMockMVCTest<AssessmentSummaryController> {
    @Mock
    private AssessmentService assessmentService;

    @Mock
    private AssessorFormInputResponseService assessorFormInputResponseService;

    @Spy
    @InjectMocks
    private AssessmentSummaryModelPopulator assessmentSummaryModelPopulator;

    @Override
    protected AssessmentSummaryController supplyControllerUnderTest() {
        return new AssessmentSummaryController();
    }

    private Long applicationId;
    private Long assessmentId;
    private Long competitionId;
    private ApplicationResource application;
    private CompetitionResource competition;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        applicationId = 1L;
        assessmentId = 2L;
        competitionId = 3L;

        String anotherTypeOfFormInputTitle = RESEARCH_CATEGORY.getTitle();

        when(assessmentService.getById(assessmentId)).thenReturn(newAssessmentResource()
                .with(id(assessmentId))
                .withApplication(applicationId)
                .withCompetition(competitionId)
                .withProcessOutcome(asList())
                .build());

        application = newApplicationResource()
                .with(id(applicationId))
                .withCompetition(competitionId)
                .build();
        when(applicationService.getById(applicationId)).thenReturn(application);

        competition = newCompetitionResource()
                .with(id(competitionId))
                .withAssessorAcceptsDate(now().minusDays(2))
                .withAssessorDeadlineDate(now().plusDays(4))
                .build();
        when(competitionService.getById(competitionId)).thenReturn(competition);

        // The first question will have no form inputs, therefore no assessment required and should not appear in the summary
        List<FormInputResource> formInputsForQuestion1 = asList();

        // The second question will have 'application in scope' type amongst the form inputs meaning that the AssessmentSummaryQuestionViewModel.applicationInScope should get populated with any response to this input
        List<FormInputResource> formInputsForQuestion2 = newFormInputResource()
                .withId(1L, 2L)
                .withFormInputTypeTitle(anotherTypeOfFormInputTitle, APPLICATION_IN_SCOPE.getTitle())
                .withQuestion(2L, 2L)
                .build(2);

        // The third question will have 'feedback' and 'score' types amongst the form inputs meaning that the AssessmentSummaryQuestionViewModel.feedback and .scoreGiven should get populated with any response to this input
        List<FormInputResource> formInputsForQuestion3 = newFormInputResource()
                .withId(3L, 4L, 5L)
                .withFormInputTypeTitle(anotherTypeOfFormInputTitle, SCORE.getTitle(), FEEDBACK.getTitle())
                .withQuestion(3L, 3L, 3L)
                .build(3);
        when(formInputService.findAssessmentInputsByCompetition(competitionId)).thenReturn(concat(concat(formInputsForQuestion1.stream(), formInputsForQuestion2.stream()), formInputsForQuestion3.stream()).collect(toList()));

        // The fourth question will have form inputs without a complete set of responses meaning that it should be incomplete
        List<FormInputResource> formInputsForQuestion4 = newFormInputResource()
                .withId(6L, 7L)
                .withFormInputTypeTitle(anotherTypeOfFormInputTitle, FEEDBACK.getTitle())
                .withQuestion(4L, 4L)
                .build(2);
        when(formInputService.findAssessmentInputsByCompetition(competitionId)).thenReturn(concat(concat(concat(formInputsForQuestion1.stream(), formInputsForQuestion2.stream()), formInputsForQuestion3.stream()), formInputsForQuestion4.stream()).collect(toList()));

        List<AssessorFormInputResponseResource> assessorResponses = newAssessorFormInputResponseResource()
                .withQuestion(2L, 2L, 3L, 3L, 3L, 4L)
                .withFormInput(1L, 2L, 3L, 4L, 5L, 6L)
                .withValue("another response", "true", "another response", "15", "feedback", "another response")
                .build(6);
        when(assessorFormInputResponseService.getAllAssessorFormInputResponses(assessmentId))
                .thenReturn(assessorResponses);

        List<QuestionResource> questions = newQuestionResource()
                .withId(1L, 2L, 3L, 4L)
                .withSection(1L, 2L, 2L, 2L)
                .withQuestionNumber(null, null, "1", "2")
                .withShortName("Application details", "Scope", "Business opportunity", "Potential market")
                .withAssessorMaximumScore(null, null, 20, null)
                .build(4);

        when(questionService.getQuestionsByAssessment(assessmentId)).thenReturn(questions);
    }

    @Test
    public void getSummary() throws Exception {
        MvcResult result = mockMvc.perform(get("/{assessmentId}/summary", assessmentId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessment/application-summary"))
                .andReturn();

        AssessmentSummaryViewModel model = (AssessmentSummaryViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(assessmentId, model.getAssessmentId());
        assertEquals(50, model.getDaysLeftPercentage());
        assertEquals(3, model.getDaysLeft());
        assertEquals(competition, model.getCompetition());
        assertEquals(application, model.getApplication());
        assertEquals(1, model.getQuestionsForScoreOverview().size());
        assertEquals(3, model.getQuestionsForReview().size());
        assertEquals(15, model.getTotalScoreGiven());
        assertEquals(20, model.getTotalScorePossible());
        assertEquals(75, model.getTotalScorePercentage());

        AssessmentSummaryQuestionViewModel scoreOverviewQuestion1 = model.getQuestionsForScoreOverview().get(0);
        assertEquals(Long.valueOf(3L), scoreOverviewQuestion1.getQuestionId());
        assertEquals("1. Business opportunity", scoreOverviewQuestion1.getDisplayLabel());
        assertEquals("Q1", scoreOverviewQuestion1.getDisplayLabelShort());
        assertTrue(scoreOverviewQuestion1.isScoreFormInputExists());
        assertEquals(Integer.valueOf(15), scoreOverviewQuestion1.getScoreGiven());
        assertEquals(Integer.valueOf(20), scoreOverviewQuestion1.getScorePossible());
        assertEquals("feedback", scoreOverviewQuestion1.getFeedback());
        assertNull(scoreOverviewQuestion1.getApplicationInScope());
        assertTrue(scoreOverviewQuestion1.isComplete());

        AssessmentSummaryQuestionViewModel reviewQuestion1 = model.getQuestionsForReview().get(0);
        assertEquals(Long.valueOf(2L), reviewQuestion1.getQuestionId());
        assertEquals("Scope", reviewQuestion1.getDisplayLabel());
        assertEquals("", reviewQuestion1.getDisplayLabelShort());
        assertFalse(reviewQuestion1.isScoreFormInputExists());
        assertNull(reviewQuestion1.getScoreGiven());
        assertNull(reviewQuestion1.getScorePossible());
        assertNull(reviewQuestion1.getFeedback());
        assertTrue(reviewQuestion1.getApplicationInScope());
        assertTrue(reviewQuestion1.isComplete());

        AssessmentSummaryQuestionViewModel reviewQuestion2 = model.getQuestionsForReview().get(1);
        assertEquals(Long.valueOf(3L), reviewQuestion2.getQuestionId());
        assertEquals("1. Business opportunity", reviewQuestion2.getDisplayLabel());
        assertEquals("Q1", reviewQuestion2.getDisplayLabelShort());
        assertTrue(reviewQuestion2.isScoreFormInputExists());
        assertEquals(Integer.valueOf(15), reviewQuestion2.getScoreGiven());
        assertEquals(Integer.valueOf(20), reviewQuestion2.getScorePossible());
        assertEquals("feedback", reviewQuestion2.getFeedback());
        assertNull(reviewQuestion2.getApplicationInScope());
        assertTrue(reviewQuestion2.isComplete());

        AssessmentSummaryQuestionViewModel reviewQuestion3 = model.getQuestionsForReview().get(2);
        assertEquals(Long.valueOf(4L), reviewQuestion3.getQuestionId());
        assertEquals("2. Potential market", reviewQuestion3.getDisplayLabel());
        assertEquals("Q2", reviewQuestion3.getDisplayLabelShort());
        assertFalse(reviewQuestion3.isScoreFormInputExists());
        assertNull(reviewQuestion3.getScoreGiven());
        assertNull(reviewQuestion3.getScorePossible());
        assertNull(reviewQuestion3.getFeedback());
        assertNull(reviewQuestion3.getApplicationInScope());
        assertFalse(reviewQuestion3.isComplete());
    }

    @Test
    public void getSummary_withExistingOutcome() throws Exception {
        Long assessmentWithExistingOutcomeId = 99L;
        Long latestProcessOutcomeId = 100L;
        String expectedFeedback = "feedback";
        String expectedComment = "comment";

        when(assessmentService.getById(assessmentWithExistingOutcomeId)).thenReturn(newAssessmentResource()
                .with(id(assessmentWithExistingOutcomeId))
                .withApplication(applicationId)
                .withCompetition(competitionId)
                .withProcessOutcome(asList(1L, 2L, 3L, latestProcessOutcomeId))
                .build());

        ProcessOutcomeResource processOutcome = newProcessOutcomeResource()
                .withOutcome("yes")
                .withDescription(expectedFeedback)
                .withComment(expectedComment)
                .build();

        when(processOutcomeService.getById(latestProcessOutcomeId)).thenReturn(processOutcome);

        AssessmentSummaryForm expectedForm = new AssessmentSummaryForm();
        expectedForm.setFundingConfirmation(TRUE);
        expectedForm.setFeedback(expectedFeedback);
        expectedForm.setComment(expectedComment);

        MvcResult result = mockMvc.perform(get("/{assessmentId}/summary", assessmentWithExistingOutcomeId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("assessment/application-summary"))
                .andReturn();

        AssessmentSummaryViewModel model = (AssessmentSummaryViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(assessmentWithExistingOutcomeId, model.getAssessmentId());
        assertEquals(competition, model.getCompetition());
        assertEquals(application, model.getApplication());
    }

    @Test
    public void save() throws Exception {
        Boolean fundingConfirmation = TRUE;
        String feedback = String.join(" ", nCopies(100, "feedback"));
        String comment = String.join(" ", nCopies(100, "comment"));

        when(assessmentService.recommend(assessmentId, fundingConfirmation, feedback, comment)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/{assessmentId}/summary", assessmentId)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("fundingConfirmation", fundingConfirmation.toString())
                .param("feedback", feedback)
                .param("comment", comment))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard/competition/" + competitionId));

        verify(assessmentService).recommend(assessmentId, fundingConfirmation, feedback, comment);
    }

    @Test
    public void save_noFundingConfirmation() throws Exception {
        String feedback = String.join(" ", nCopies(100, "feedback"));
        String comment = String.join(" ", nCopies(100, "comment"));

        MvcResult result = mockMvc.perform(post("/{assessmentId}/summary", assessmentId)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("feedback", feedback)
                .param("comment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "fundingConfirmation"))
                .andExpect(view().name("assessment/application-summary"))
                .andReturn();

        AssessmentSummaryForm form = (AssessmentSummaryForm) result.getModelAndView().getModel().get("form");

        assertNull(form.getFundingConfirmation());
        assertEquals(feedback, form.getFeedback());
        assertEquals(comment, form.getComment());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("fundingConfirmation"));
        assertEquals("Please indicate your decision", bindingResult.getFieldError("fundingConfirmation").getDefaultMessage());

        verify(assessmentService).getById(assessmentId);
        verifyNoMoreInteractions(assessmentService);
    }

    @Test
    public void save_noFeedbackAndFundingConfirmationIsTrue() throws Exception {
        Boolean fundingConfirmation = TRUE;
        String comment = String.join(" ", nCopies(100, "comment"));

        when(assessmentService.recommend(assessmentId, fundingConfirmation, null, comment)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/{assessmentId}/summary", assessmentId)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("fundingConfirmation", fundingConfirmation.toString())
                .param("comment", comment))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard/competition/" + competitionId));

        verify(assessmentService).recommend(assessmentId, fundingConfirmation, null, comment);
    }

    @Test
    public void save_noFeedbackAndFundingConfirmationIsFalse() throws Exception {
        Boolean fundingConfirmation = FALSE;
        String comment = String.join(" ", nCopies(100, "comment"));

        MvcResult result = mockMvc.perform(post("/{assessmentId}/summary", assessmentId)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("fundingConfirmation", fundingConfirmation.toString())
                .param("comment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form"))
                .andExpect(view().name("assessment/application-summary"))
                .andReturn();

        AssessmentSummaryForm form = (AssessmentSummaryForm) result.getModelAndView().getModel().get("form");

        assertEquals(fundingConfirmation, form.getFundingConfirmation());
        assertNull(form.getFeedback());
        assertEquals(comment, form.getComment());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("feedback"));
        assertEquals("Please enter your feedback", bindingResult.getFieldError("feedback").getDefaultMessage());

        verify(assessmentService).getById(assessmentId);
        verifyNoMoreInteractions(assessmentService);
    }

    @Test
    public void save_noComment() throws Exception {
        Boolean fundingConfirmation = TRUE;
        String feedback = String.join(" ", nCopies(100, "feedback"));

        when(assessmentService.recommend(assessmentId, fundingConfirmation, feedback, null)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/{assessmentId}/summary", assessmentId)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("fundingConfirmation", fundingConfirmation.toString())
                .param("feedback", feedback))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard/competition/" + competitionId));

        verify(assessmentService).recommend(assessmentId, fundingConfirmation, feedback, null);
    }

    @Test
    public void save_exceedsCharacterSizeLimit() throws Exception {
        Boolean fundingConfirmation = TRUE;
        String feedback = RandomStringUtils.random(5001);
        String comment = RandomStringUtils.random(5001);

        MvcResult result = mockMvc.perform(post("/{assessmentId}/summary", assessmentId)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("fundingConfirmation", fundingConfirmation.toString())
                .param("feedback", feedback)
                .param("comment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "feedback"))
                .andExpect(model().attributeHasFieldErrors("form", "comment"))
                .andExpect(view().name("assessment/application-summary"))
                .andReturn();

        AssessmentSummaryForm form = (AssessmentSummaryForm) result.getModelAndView().getModel().get("form");

        assertEquals(fundingConfirmation, form.getFundingConfirmation());
        assertEquals(feedback, form.getFeedback());
        assertEquals(comment, form.getComment());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(2, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("feedback"));
        assertTrue(bindingResult.hasFieldErrors("comment"));
        assertEquals("This field cannot contain more than {1} characters", bindingResult.getFieldError("feedback").getDefaultMessage());
        assertEquals(5000, bindingResult.getFieldError("feedback").getArguments()[1]);
        assertEquals("This field cannot contain more than {1} characters", bindingResult.getFieldError("comment").getDefaultMessage());
        assertEquals(5000, bindingResult.getFieldError("comment").getArguments()[1]);

        verify(assessmentService).getById(assessmentId);
        verifyNoMoreInteractions(assessmentService);
    }

    @Test
    public void save_exceedsWordLimit() throws Exception {
        Boolean fundingConfirmation = TRUE;
        String feedback = String.join(" ", nCopies(101, "feedback"));
        String comment = String.join(" ", nCopies(101, "comment"));

        MvcResult result = mockMvc.perform(post("/{assessmentId}/summary", assessmentId)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("fundingConfirmation", fundingConfirmation.toString())
                .param("feedback", feedback)
                .param("comment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "feedback"))
                .andExpect(model().attributeHasFieldErrors("form", "comment"))
                .andExpect(view().name("assessment/application-summary"))
                .andReturn();

        AssessmentSummaryForm form = (AssessmentSummaryForm) result.getModelAndView().getModel().get("form");

        assertEquals(fundingConfirmation, form.getFundingConfirmation());
        assertEquals(feedback, form.getFeedback());
        assertEquals(comment, form.getComment());

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(2, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("feedback"));
        assertTrue(bindingResult.hasFieldErrors("comment"));
        assertEquals("Maximum word count exceeded. Please reduce your word count to {1}.", bindingResult.getFieldError("feedback").getDefaultMessage());
        assertEquals(100, bindingResult.getFieldError("feedback").getArguments()[1]);
        assertEquals("Maximum word count exceeded. Please reduce your word count to {1}.", bindingResult.getFieldError("comment").getDefaultMessage());
        assertEquals(100, bindingResult.getFieldError("comment").getArguments()[1]);

        verify(assessmentService).getById(assessmentId);
        verifyNoMoreInteractions(assessmentService);
    }
}
