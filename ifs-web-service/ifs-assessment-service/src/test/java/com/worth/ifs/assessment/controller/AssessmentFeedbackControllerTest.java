package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.assessment.form.AssessmentFeedbackForm;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.service.AssessmentFeedbackService;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.assessment.viewmodel.AssessmentFeedbackApplicationDetailsModel;
import com.worth.ifs.assessment.viewmodel.AssessmentFeedbackViewModel;
import com.worth.ifs.assessment.viewmodel.AssessmentNavigationViewModel;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputTypeResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import static com.worth.ifs.BaseBuilderAmendFunctions.idBasedValues;
import static com.worth.ifs.assessment.builder.AssessmentFeedbackResourceBuilder.newAssessmentFeedbackResource;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static com.worth.ifs.form.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static com.worth.ifs.util.CollectionFunctions.simpleToMap;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
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
    private static final Map<Long, FormInputTypeResource> FORM_INPUT_TYPES = simpleToMap(asList(new FormInputTypeResource(1L, "textarea"), new FormInputTypeResource(5L, "application_details")), FormInputTypeResource::getId);

    @Before
    public void setUp() {
        super.setUp();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupAssessment(PROCESS_ROLE_ID);
    }

    @Override
    protected AssessmentFeedbackController supplyControllerUnderTest() {
        return new AssessmentFeedbackController();
    }

    @Test
    public void testGetQuestion() throws Exception {
        final Long expectedPreviousQuestionId = 10L;
        final Long expectedNextQuestionId = 21L;
        final QuestionResource expectedQuestion = questionResources.get(QUESTION_ID);
        final String expectedValue = "Blah";
        final Integer expectedScore = 10;
        final AssessmentFeedbackForm expectedForm = new AssessmentFeedbackForm(expectedValue, expectedScore, null, null);
        final AssessmentNavigationViewModel expectedNavigation = new AssessmentNavigationViewModel(ASSESSMENT_ID, of(questionResources.get(expectedPreviousQuestionId)), of(questionResources.get(expectedNextQuestionId)));

        this.setupApplicationResponses(APPLICATION_ID, QUESTION_ID, FORM_INPUT_TYPES.get(1L));

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
        assertTrue(model.isFeedbackRequired());
        assertEquals(expectedQuestion, model.getQuestion());
        assertEquals(expectedQuestion.getFormInputs(), model.getQuestionFormInputs().stream().map(FormInputResource::getId).collect(Collectors.toList()));
        expectedQuestion.getFormInputs().forEach(formInput ->
                assertTrue("Form input response map should contain entry key for form input with id: " + formInput, model.getQuestionFormInputResponses().containsKey(formInput))
        );

        verify(assessmentService, only()).getById(ASSESSMENT_ID);
        verify(processRoleService, only()).getById(PROCESS_ROLE_ID);
        verify(applicationService, only()).getById(APPLICATION_ID);
        verify(competitionService, only()).getById(competitionResource.getId());
        verify(questionService, atLeast(1)).getById(same(QUESTION_ID));
        verify(formInputService, times(2)).findByQuestion(QUESTION_ID);
        questionResources.get(QUESTION_ID).getFormInputs().forEach(formInput -> verify(formInputResponseService, times(1)).getByFormInputIdAndApplication(formInput, APPLICATION_ID));
        verify(questionService, times(1)).getPreviousQuestion(QUESTION_ID);
        verify(questionService, times(1)).getNextQuestion(QUESTION_ID);
        verify(assessmentFeedbackService, only()).getAssessmentFeedbackByAssessmentAndQuestion(ASSESSMENT_ID, QUESTION_ID);
    }

    @Test
    public void testGetQuestion_applicationDetailsQuestion() throws Exception {
        final Long expectedNextQuestionId = 10L;
        final Long applicationDetailsQuestionId = 1L;
        final ApplicationResource expectedApplication = simpleToMap(applications, ApplicationResource::getId).get(APPLICATION_ID);
        final QuestionResource expectedQuestion = questionResources.get(applicationDetailsQuestionId);
        final AssessmentFeedbackApplicationDetailsModel expectedModel = new AssessmentFeedbackApplicationDetailsModel(competitionResource, expectedApplication, expectedQuestion);
        final AssessmentNavigationViewModel expectedNavigation = new AssessmentNavigationViewModel(ASSESSMENT_ID, empty(), of(questionResources.get(expectedNextQuestionId)));

        this.setupApplicationResponses(APPLICATION_ID, applicationDetailsQuestionId, FORM_INPUT_TYPES.get(5L));
        this.setupInvites();

        final MvcResult result = mockMvc.perform(get("/{assessmentId}/question/{questionId}", ASSESSMENT_ID, applicationDetailsQuestionId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(model().attribute("model", expectedModel))
                .andExpect(model().attribute("navigation", expectedNavigation))
                .andExpect(view().name("assessment-application-details"))
                .andReturn();

        final AssessmentFeedbackApplicationDetailsModel model = (AssessmentFeedbackApplicationDetailsModel) result.getModelAndView().getModel().get("model");
        assertEquals(50, model.getDaysLeftPercentage());
        assertEquals(3, model.getDaysLeft());

        verify(assessmentService, only()).getById(ASSESSMENT_ID);
        verify(processRoleService, times(1)).getById(PROCESS_ROLE_ID);
        verify(applicationService, only()).getById(APPLICATION_ID);
        verify(competitionService, only()).getById(competitionResource.getId());
        verify(formInputService, only()).findByQuestion(applicationDetailsQuestionId);
        verify(formInputResponseService, never()).getByFormInputIdAndApplication(anyLong(), anyLong());
        verify(questionService, times(1)).getPreviousQuestion(applicationDetailsQuestionId);
        verify(questionService, times(1)).getNextQuestion(applicationDetailsQuestionId);
        verify(assessmentFeedbackService, never()).getAssessmentFeedbackByAssessmentAndQuestion(anyLong(), anyLong());
    }

    @Test
    public void testUpdateFeedbackValue() throws Exception {
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
    public void testUpdateScore() throws Exception {
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
    public void testSave() throws Exception {
        final String value = "Blah";
        final Integer score = 10;

        mockMvc.perform(post("/{assessmentId}/question/{questionId}", ASSESSMENT_ID, QUESTION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("value", value)
                .param("score", String.valueOf(score)))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/" + ASSESSMENT_ID))
                .andReturn();

        verify(assessmentFeedbackService, only()).updateAssessmentFeedback(ASSESSMENT_ID, QUESTION_ID, value, score);
    }

    @Override
    public void setupCompetition() {
        super.setupCompetition();

        competitionResource.setAssessmentStartDate(LocalDateTime.now().minusDays(2));
        competitionResource.setAssessmentEndDate(LocalDateTime.now().plusDays(4));
        questionResources.get(QUESTION_ID).setShortName("Market opportunity");
        questionResources.get(QUESTION_ID).setNeedingAssessorFeedback(true);
    }

    private FormInputResource setupFormInput(final Long questionId, final FormInputTypeResource formInputType) {
        final Long formInputId = questionResources.get(questionId).getFormInputs().get(0);
        final FormInputResource formInput = newFormInputResource()
                .withId(formInputId)
                .withFormInputType(formInputType.getId())
                .withFormInputTypeTitle(formInputType.getTitle())
                .build();
        when(formInputService.findByQuestion(questionId)).thenReturn(singletonList(formInput));
        return formInput;
    }

    private void setupApplicationResponses(final Long applicationId, final Long questionId, final FormInputTypeResource formInputType) {
        setupApplicationResponses(applicationId, setupFormInput(questionId, formInputType));
    }

    private void setupApplicationResponses(final Long applicationId, final FormInputResource formInput) {
        when(formInputResponseService.getByFormInputIdAndApplication(formInput.getId(), applicationId)).thenReturn(restSuccess(
                newFormInputResponseResource()
                        .withFormInputs(formInput.getId())
                        .with(idBasedValues("Value "))
                        .build(1))
        );
    }

    private void setupAssessment(final Long processRoleId) {
        final AssessmentResource assessment = newAssessmentResource()
                .withId(1L)
                .withProcessRole(processRoleId)
                .build();

        when(assessmentService.getById(assessment.getId())).thenReturn(assessment);
    }
}