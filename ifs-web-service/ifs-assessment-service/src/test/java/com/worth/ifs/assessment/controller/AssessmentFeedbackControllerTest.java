package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.assessment.form.AssessmentFeedbackForm;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.service.AssessmentFeedbackService;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.assessment.viewmodel.AssessmentFeedbackViewModel;
import com.worth.ifs.assessment.viewmodel.AssessmentNavigationViewModel;
import com.worth.ifs.form.resource.FormInputResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.worth.ifs.BaseBuilderAmendFunctions.idBasedValues;
import static com.worth.ifs.assessment.builder.AssessmentFeedbackResourceBuilder.newAssessmentFeedbackResource;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static com.worth.ifs.form.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static java.util.Optional.of;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessmentFeedbackControllerTest extends BaseControllerMockMVCTest<AssessmentFeedbackController> {
    @Mock
    private AssessmentService assessmentService;

    @Mock
    private AssessmentFeedbackService assessmentFeedbackService;

    private static final Long APPLICATION_ID = 2L; // "Providing sustainable childcare"

    private static final Long QUESTION_ID = 20L; // 1. What is the business opportunity that this project addresses?
    private static final Long PROCESS_ROLE_ID = 6L;
    private static final Long ASSESSMENT_ID = 1L;

    @Before
    public void setUp() {
        super.setUp();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses(APPLICATION_ID, QUESTION_ID);
        this.setupAssessment(PROCESS_ROLE_ID);
    }

    @Override
    protected AssessmentFeedbackController supplyControllerUnderTest() {
        return new AssessmentFeedbackController();
    }

    @Test
    public void getQuestion() throws Exception {
        final Long expectedPreviousQuestionId = 10L;
        final Long expectedNextQuestionId = 21L;
        final QuestionResource expectedQuestion = questionResources.get(QUESTION_ID);
        final String expectedValue = "Blah";
        final Integer expectedScore = 10;
        final AssessmentFeedbackForm expectedForm = new AssessmentFeedbackForm(expectedValue, expectedScore, null, null);
        final AssessmentNavigationViewModel expectedNavigation = new AssessmentNavigationViewModel(ASSESSMENT_ID, of(questionResources.get(expectedPreviousQuestionId)), of(questionResources.get(expectedNextQuestionId)));

        when(assessmentFeedbackService.getAssessmentFeedbackByAssessmentAndQuestion(ASSESSMENT_ID, QUESTION_ID)).thenReturn(newAssessmentFeedbackResource()
                .withFeedback(expectedValue)
                .withScore(expectedScore)
                .build());

        final MvcResult result = mockMvc.perform(get("/{assessmentId}/question/{questionId}", ASSESSMENT_ID, QUESTION_ID))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().attribute("navigation", expectedNavigation))
                .andExpect(view().name("assessment-question"))
                .andReturn();

        final AssessmentFeedbackViewModel model = (AssessmentFeedbackViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(50, model.getDaysLeftPercentage());
        assertEquals(3, model.getDaysLeft());
        assertEquals("Market opportunity", model.getTitle());
        assertEquals(expectedQuestion, model.getQuestion());
        assertEquals(expectedQuestion.getFormInputs(), model.getQuestionFormInputs().stream().map(FormInputResource::getId).collect(Collectors.toList()));
        expectedQuestion.getFormInputs().forEach(formInput ->
                assertTrue("Form input response map should contain entry key for form input with id: " + formInput, model.getQuestionFormInputResponses().containsKey(String.valueOf(formInput)))
        );

        verify(assessmentService, only()).getById(ASSESSMENT_ID);
        verify(processRoleService, only()).getById(PROCESS_ROLE_ID);
        verify(applicationService, only()).getById(APPLICATION_ID);
        verify(competitionService, only()).getById(competitionResource.getId());
        verify(questionService, atLeast(1)).getById(same(QUESTION_ID));
        verify(formInputService, only()).findByQuestion(QUESTION_ID);
        questionResources.get(QUESTION_ID).getFormInputs().forEach(formInput -> verify(formInputResponseService, times(1)).getByFormInputIdAndApplication(formInput, APPLICATION_ID));
        verify(questionService, times(1)).getPreviousQuestion(QUESTION_ID);
        verify(questionService, times(1)).getNextQuestion(QUESTION_ID);
        verify(assessmentFeedbackService, only()).getAssessmentFeedbackByAssessmentAndQuestion(ASSESSMENT_ID, QUESTION_ID);
    }

    @Test
    public void updateFeedbackValue() throws Exception {
        final String value = "Blah";
        when(assessmentFeedbackService.updateFeedbackValue(ASSESSMENT_ID, QUESTION_ID, value)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/{assessmentId}/question/{questionId}/feedback-value", ASSESSMENT_ID, QUESTION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("value", value))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success", is("true")))
                .andReturn();

        verify(assessmentFeedbackService, only()).updateFeedbackValue(ASSESSMENT_ID, QUESTION_ID, value);
    }

    @Test
    public void updateScore() throws Exception {
        final Integer score = 10;
        when(assessmentFeedbackService.updateFeedbackScore(ASSESSMENT_ID, QUESTION_ID, score)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/{assessmentId}/question/{questionId}/feedback-score", ASSESSMENT_ID, QUESTION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("score", String.valueOf(score)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success", is("true")))
                .andReturn();

        verify(assessmentFeedbackService, only()).updateFeedbackScore(ASSESSMENT_ID, QUESTION_ID, score);
    }

    @Test
    public void save() throws Exception {
        final String value = "Blah";
        final Integer score = 10;

        mockMvc.perform(post("/{assessmentId}/question/{questionId}", ASSESSMENT_ID, QUESTION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("value", value)
                .param("score", String.valueOf(score)))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/" + ASSESSMENT_ID))
                .andReturn();

        // TODO Verify save call
    }

    @Override
    public void setupCompetition() {
        super.setupCompetition();

        competitionResource.setAssessmentStartDate(LocalDateTime.now().minusDays(2));
        competitionResource.setAssessmentEndDate(LocalDateTime.now().plusDays(4));
        questionResources.get(QUESTION_ID).setShortName("Market opportunity");
    }

    private void setupApplicationResponses(final Long applicationId, final Long questionId) {
        final List<Long> formInputIds = questionResources.get(questionId).getFormInputs(); // already setup
        final List<FormInputResource> formInputs = newFormInputResource().withId(formInputIds.stream().toArray(Long[]::new)).build(formInputIds.size());
        when(formInputService.findByQuestion(questionId)).thenReturn(formInputs);

        formInputs.forEach(formInput -> when(formInputResponseService.getByFormInputIdAndApplication(formInput.getId(), applicationId)).thenReturn(restSuccess(
                newFormInputResponseResource()
                        .withFormInputs(formInput.getId())
                        .with(idBasedValues("Value "))
                        .build(1))
        ));
    }

    private void setupAssessment(final Long processRoleId) {
        final AssessmentResource assessment = newAssessmentResource()
                .withId(1L)
                .withProcessRole(processRoleId)
                .build();

        when(assessmentService.getById(assessment.getId())).thenReturn(assessment);
    }
}