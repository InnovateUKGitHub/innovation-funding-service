package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.form.Form;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.assessment.model.AssessmentFeedbackApplicationDetailsModelPopulator;
import com.worth.ifs.assessment.model.AssessmentFeedbackModelPopulator;
import com.worth.ifs.assessment.model.AssessmentFeedbackNavigationModelPopulator;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.assessment.service.AssessorFormInputResponseService;
import com.worth.ifs.assessment.viewmodel.AssessmentFeedbackApplicationDetailsViewModel;
import com.worth.ifs.assessment.viewmodel.AssessmentFeedbackViewModel;
import com.worth.ifs.assessment.viewmodel.AssessmentNavigationViewModel;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.form.resource.FormInputTypeResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.worth.ifs.BaseBuilderAmendFunctions.idBasedValues;
import static com.worth.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static com.worth.ifs.commons.error.Error.fieldError;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static com.worth.ifs.form.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static com.worth.ifs.util.CollectionFunctions.simpleToMap;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
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
    private AssessorFormInputResponseService assessorFormInputResponseService;

    @Spy
    @InjectMocks
    private AssessmentFeedbackModelPopulator assessmentFeedbackModelPopulator;

    @Spy
    @InjectMocks
    private AssessmentFeedbackNavigationModelPopulator assessmentFeedbackNavigationModelPopulator;

    @Spy
    @InjectMocks
    private AssessmentFeedbackApplicationDetailsModelPopulator assessmentFeedbackApplicationDetailsModelPopulator;

    private static Long APPLICATION_ID = 2L; // "Providing sustainable childcare"
    private static Long QUESTION_ID = 20L; // 1. What is the business opportunity that this project addresses?
    private static Long APPLICATION_DETAILS_QUESTION_ID = 1L;
    private static Long ASSESSMENT_ID = 1L;
    private static Map<String, FormInputTypeResource> FORM_INPUT_TYPES = simpleToMap(asList(
            new FormInputTypeResource(1L, "textarea"),
            new FormInputTypeResource(2L, "application_details"),
            new FormInputTypeResource(3L, "assessor_score")
    ), FormInputTypeResource::getTitle);

    @Before
    public void setUp() {
        super.setUp();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupAssessment(APPLICATION_ID);
    }

    @Override
    protected AssessmentFeedbackController supplyControllerUnderTest() {
        return new AssessmentFeedbackController();
    }

    @Test
    public void getQuestion() throws Exception {
        Long expectedPreviousQuestionId = 10L;
        Long expectedNextQuestionId = 21L;
        Long sectionId = 2L;
        CompetitionResource expectedCompetition = competitionResource;
        ApplicationResource expectedApplication = simpleToMap(applications, ApplicationResource::getId).get(APPLICATION_ID);

        List<FormInputResource> applicationFormInputs = this.setupApplicationFormInputs(QUESTION_ID, FORM_INPUT_TYPES.get("textarea"));
        this.setupApplicantResponses(APPLICATION_ID, applicationFormInputs);

        List<FormInputResource> assessmentFormInputs = this.setupAssessmentFormInputs(QUESTION_ID, FORM_INPUT_TYPES.get("textarea"), FORM_INPUT_TYPES.get("assessor_score"));
        List<AssessorFormInputResponseResource> assessorResponses = this.setupAssessorResponses(ASSESSMENT_ID, QUESTION_ID, assessmentFormInputs);

        Form expectedForm = new Form();
        expectedForm.setFormInput(simpleToMap(assessorResponses, assessorFormInputResponseResource -> String.valueOf(assessorFormInputResponseResource.getFormInput()), AssessorFormInputResponseResource::getValue));
        AssessmentNavigationViewModel expectedNavigation = new AssessmentNavigationViewModel(ASSESSMENT_ID, of(questionResources.get(expectedPreviousQuestionId)), of(questionResources.get(expectedNextQuestionId)));
        this.setupNextQuestionSection(sectionId, expectedNextQuestionId, true);

        MvcResult result = mockMvc.perform(get("/{assessmentId}/question/{questionId}", ASSESSMENT_ID, QUESTION_ID))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().attribute("navigation", expectedNavigation))
                .andExpect(view().name("assessment/application-question"))
                .andReturn();

        AssessmentFeedbackViewModel model = (AssessmentFeedbackViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(50, model.getDaysLeftPercentage());
        assertEquals(3, model.getDaysLeft());
        assertEquals(expectedCompetition, model.getCompetition());
        assertEquals(expectedApplication, model.getApplication());
        assertEquals(QUESTION_ID, model.getQuestionId());
        assertEquals("1", model.getQuestionNumber());
        assertEquals("Market opportunity", model.getQuestionShortName());
        assertEquals("1. What is the business opportunity that this project addresses?", model.getQuestionName());
        assertEquals(Integer.valueOf(50), model.getMaximumScore());
        assertEquals("Value 64", model.getApplicantResponse());
        assertEquals(assessmentFormInputs, model.getAssessmentFormInputs());
        assertTrue(model.isScoreFormInputExists());
        assertFalse(model.isScopeFormInputExists());
        assertFalse(model.isAppendixExists());
        assertNull(model.getAppendixDetails());

        verify(assessmentService, only()).getById(ASSESSMENT_ID);
        verify(applicationService, only()).getById(APPLICATION_ID);
        verify(competitionService, only()).getById(competitionResource.getId());
        verify(questionService, atLeast(1)).getById(same(QUESTION_ID));
        verify(formInputService, times(2)).findApplicationInputsByQuestion(QUESTION_ID);
        verify(formInputService, times(1)).findAssessmentInputsByQuestion(QUESTION_ID);
        applicationFormInputs.forEach(formInput -> verify(formInputResponseService, times(1)).getByFormInputIdAndApplication(formInput.getId(), APPLICATION_ID));
        verify(questionService, times(1)).getPreviousQuestion(QUESTION_ID);
        verify(questionService, times(1)).getNextQuestion(QUESTION_ID);
        verify(assessorFormInputResponseService, only()).getAllAssessorFormInputResponsesByAssessmentAndQuestion(ASSESSMENT_ID, QUESTION_ID);
    }

    @Test
    public void nextQuestionIsNotNavigable() throws Exception {
        Long expectedPreviousQuestionId = 10L;
        Long expectedNextQuestionId = 21L;
        Long sectionId = 71L;
        List<FormInputResource> applicationFormInputs = this.setupApplicationFormInputs(QUESTION_ID, FORM_INPUT_TYPES.get("textarea"));
        this.setupApplicantResponses(APPLICATION_ID, applicationFormInputs);

        List<FormInputResource> assessmentFormInputs = this.setupAssessmentFormInputs(QUESTION_ID, FORM_INPUT_TYPES.get("textarea"), FORM_INPUT_TYPES.get("assessor_score"));
        List<AssessorFormInputResponseResource> assessorResponses = this.setupAssessorResponses(ASSESSMENT_ID, QUESTION_ID, assessmentFormInputs);

        Form expectedForm = new Form();
        expectedForm.setFormInput(simpleToMap(assessorResponses, assessorFormInputResponseResource -> String.valueOf(assessorFormInputResponseResource.getFormInput()), AssessorFormInputResponseResource::getValue));
        AssessmentNavigationViewModel expectedNavigation = new AssessmentNavigationViewModel(ASSESSMENT_ID, of(questionResources.get(expectedPreviousQuestionId)), Optional.empty());
        this.setupNextQuestionSection(sectionId, expectedNextQuestionId, false);

        MvcResult result = mockMvc.perform(get("/{assessmentId}/question/{questionId}", ASSESSMENT_ID, QUESTION_ID))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().attribute("navigation", expectedNavigation))
                .andExpect(view().name("assessment/application-question"))
                .andReturn();
    }

    @Test
    public void getQuestion_applicationDetailsQuestion() throws Exception {
        Long expectedNextQuestionId = 10L;
        Long sectionId = 2L;
        CompetitionResource expectedCompetition = competitionResource;
        ApplicationResource expectedApplication = simpleToMap(applications, ApplicationResource::getId).get(APPLICATION_ID);
        AssessmentNavigationViewModel expectedNavigation = new AssessmentNavigationViewModel(ASSESSMENT_ID, empty(), of(questionResources.get(expectedNextQuestionId)));

        List<FormInputResource> applicationFormInputs = this.setupApplicationFormInputs(APPLICATION_DETAILS_QUESTION_ID, FORM_INPUT_TYPES.get("application_details"));
        this.setupApplicantResponses(APPLICATION_ID, applicationFormInputs);
        this.setupInvites();
        this.setupNextQuestionSection(sectionId, expectedNextQuestionId, true);

        MvcResult result = mockMvc.perform(get("/{assessmentId}/question/{questionId}", ASSESSMENT_ID, APPLICATION_DETAILS_QUESTION_ID))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(model().attribute("navigation", expectedNavigation))
                .andExpect(view().name("assessment/application-details"))
                .andReturn();

        AssessmentFeedbackApplicationDetailsViewModel model = (AssessmentFeedbackApplicationDetailsViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(50, model.getDaysLeftPercentage());
        assertEquals(3, model.getDaysLeft());
        assertEquals(expectedCompetition, model.getCompetition());
        assertEquals(expectedApplication, model.getApplication());
        assertEquals("Application details", model.getQuestionShortName());

        verify(assessmentService, only()).getById(ASSESSMENT_ID);
        verify(applicationService, only()).getById(APPLICATION_ID);
        verify(competitionService, only()).getById(competitionResource.getId());
        verify(formInputService, only()).findApplicationInputsByQuestion(APPLICATION_DETAILS_QUESTION_ID);
        verify(formInputService, never()).findAssessmentInputsByQuestion(APPLICATION_DETAILS_QUESTION_ID);
        verify(formInputResponseService, never()).getByFormInputIdAndApplication(anyLong(), anyLong());
        verify(questionService, times(1)).getPreviousQuestion(APPLICATION_DETAILS_QUESTION_ID);
        verify(questionService, times(1)).getNextQuestion(APPLICATION_DETAILS_QUESTION_ID);
        verify(assessorFormInputResponseService, never()).getAllAssessorFormInputResponsesByAssessmentAndQuestion(anyLong(), anyLong());
    }

    @Test
    public void updateFormInputResponse() throws Exception {
        String value = "Feedback";
        Long formInputId = 1L;
        when(assessorFormInputResponseService.updateFormInputResponse(ASSESSMENT_ID, formInputId, value)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/{assessmentId}/formInput/{formInputId}", ASSESSMENT_ID, formInputId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("value", value))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success", is("true")))
                .andReturn();

        verify(assessorFormInputResponseService, only()).updateFormInputResponse(ASSESSMENT_ID, formInputId, value);
    }

    @Test
    public void updateFormInputResponse_exceedsCharacterSizeLimit() throws Exception {
        String value = "This is the feedback";
        Long formInputId = 1L;

        when(assessorFormInputResponseService.updateFormInputResponse(ASSESSMENT_ID, formInputId, value)).thenReturn(serviceFailure(fieldError("value", "Feedback", "validation.field.too.many.characters", "", "5000", "0")));

        when(messageSource.getMessage("validation.field.too.many.characters", new Object[]{"", "5000", "0"}, Locale.UK)).thenReturn("This field cannot contain more than 5000 characters");

        MvcResult result = mockMvc.perform(post("/{assessmentId}/formInput/{formInputId}", ASSESSMENT_ID, formInputId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("value", value))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success", is("false")))
                .andReturn();

        verify(assessorFormInputResponseService, only()).updateFormInputResponse(ASSESSMENT_ID, formInputId, value);
        String content = result.getResponse().getContentAsString();
        String jsonExpectedContent = "{\"success\":\"false\",\"validation_errors\":[\"This field cannot contain more than 5000 characters\"]}";
        assertEquals(jsonExpectedContent, content);
    }

    @Test
    public void updateFormInputResponse_exceedWordLimit() throws Exception {
        String value = "This is the feedback";
        Long formInputId = 1L;

        when(assessorFormInputResponseService.updateFormInputResponse(ASSESSMENT_ID, formInputId, value)).thenReturn(serviceFailure(fieldError("value", "Feedback", "validation.field.max.word.count", "", 100)));

        when(messageSource.getMessage("validation.field.max.word.count", new Object[]{"", "100"}, Locale.UK)).thenReturn("Maximum word count exceeded. Please reduce your word count to 100.");

        MvcResult result = mockMvc.perform(post("/{assessmentId}/formInput/{formInputId}", ASSESSMENT_ID, formInputId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("value", value))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success", is("false")))
                .andReturn();

        verify(assessorFormInputResponseService, only()).updateFormInputResponse(ASSESSMENT_ID, formInputId, value);
        String content = result.getResponse().getContentAsString();
        String jsonExpectedContent = "{\"success\":\"false\",\"validation_errors\":[\"Maximum word count exceeded. Please reduce your word count to 100.\"]}";
        assertEquals(jsonExpectedContent, content);
    }

    @Test
    public void save() throws Exception {
        List<FormInputResource> formInputs = this.setupAssessmentFormInputs(QUESTION_ID, FORM_INPUT_TYPES.get("assessor_score"), FORM_INPUT_TYPES.get("textarea"));

        Long formInputIdScore = formInputs.get(0).getId();
        Long formInputIdFeedback = formInputs.get(1).getId();
        String formInputScoreField = format("formInput[%s]", formInputIdScore);
        String formInputFeedbackField = format("formInput[%s]", formInputIdFeedback);

        when(assessorFormInputResponseService.updateFormInputResponse(ASSESSMENT_ID, formInputIdScore, "10")).thenReturn(serviceSuccess());
        when(assessorFormInputResponseService.updateFormInputResponse(ASSESSMENT_ID, formInputIdFeedback, "Feedback")).thenReturn(serviceSuccess());

        mockMvc.perform(post("/{assessmentId}/question/{questionId}", ASSESSMENT_ID, QUESTION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param(formInputScoreField, "10")
                .param(formInputFeedbackField, "Feedback"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/%s", ASSESSMENT_ID)))
                .andReturn();

        verify(assessorFormInputResponseService, times(1)).updateFormInputResponse(ASSESSMENT_ID, formInputIdScore, "10");
        verify(assessorFormInputResponseService, times(1)).updateFormInputResponse(ASSESSMENT_ID, formInputIdFeedback, "Feedback");
    }

    @Test
    public void save_exceedsCharacterSizeLimit() throws Exception {
        Long expectedNextQuestionId = 21L;
        Long sectionId = 71L;
        List<FormInputResource> formInputs = this.setupAssessmentFormInputs(QUESTION_ID, FORM_INPUT_TYPES.get("assessor_score"), FORM_INPUT_TYPES.get("textarea"));

        Long formInputIdScore = formInputs.get(0).getId();
        Long formInputIdFeedback = formInputs.get(1).getId();
        String formInputScoreField = format("formInput[%s]", formInputIdScore);
        String formInputFeedbackField = format("formInput[%s]", formInputIdFeedback);

        when(assessorFormInputResponseService.updateFormInputResponse(ASSESSMENT_ID, formInputIdScore, "10")).thenReturn(serviceSuccess());
        when(assessorFormInputResponseService.updateFormInputResponse(ASSESSMENT_ID, formInputIdFeedback, "Feedback")).thenReturn(serviceFailure(fieldError("value", "Feedback", "validation.field.too.many.characters", "", "5000", "0")));

        // For re-display of question view following the invalid data entry
        List<FormInputResource> applicationFormInputs = this.setupApplicationFormInputs(QUESTION_ID, FORM_INPUT_TYPES.get("textarea"));
        this.setupApplicantResponses(APPLICATION_ID, applicationFormInputs);
        this.setupNextQuestionSection(sectionId, expectedNextQuestionId, true);

        MvcResult result = mockMvc.perform(post("/{assessmentId}/question/{questionId}", ASSESSMENT_ID, QUESTION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param(formInputScoreField, "10")
                .param(formInputFeedbackField, "Feedback"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form"))
                .andExpect(view().name("assessment/application-question"))
                .andReturn();

        verify(assessorFormInputResponseService, times(1)).updateFormInputResponse(ASSESSMENT_ID, formInputIdScore, "10");
        verify(assessorFormInputResponseService, times(1)).updateFormInputResponse(ASSESSMENT_ID, formInputIdFeedback, "Feedback");

        Form form = (Form) result.getModelAndView().getModel().get("form");

        assertEquals("10", form.getFormInput(formInputIdScore.toString()));
        assertEquals("Feedback", form.getFormInput(formInputIdFeedback.toString()));

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors(formInputFeedbackField));
        assertEquals("validation.field.too.many.characters", bindingResult.getFieldError(formInputFeedbackField).getCode());
        assertEquals("5000", bindingResult.getFieldError(formInputFeedbackField).getArguments()[1]);
        assertEquals("0", bindingResult.getFieldError(formInputFeedbackField).getArguments()[2]);
    }

    @Test
    public void save_exceedWordLimit() throws Exception {
        Long expectedNextQuestionId = 21L;
        Long sectionId = 71L;
        List<FormInputResource> formInputs = this.setupAssessmentFormInputs(QUESTION_ID, FORM_INPUT_TYPES.get("assessor_score"), FORM_INPUT_TYPES.get("textarea"));

        Long formInputIdScore = formInputs.get(0).getId();
        Long formInputIdFeedback = formInputs.get(1).getId();
        String formInputScoreField = format("formInput[%s]", formInputIdScore);
        String formInputFeedbackField = format("formInput[%s]", formInputIdFeedback);

        when(assessorFormInputResponseService.updateFormInputResponse(ASSESSMENT_ID, formInputIdScore, "10")).thenReturn(serviceSuccess());
        when(assessorFormInputResponseService.updateFormInputResponse(ASSESSMENT_ID, formInputIdFeedback, "Feedback")).thenReturn(serviceFailure(fieldError("value", "Feedback", "validation.field.max.word.count", "", 100)));

        // For re-display of question view following the invalid data entry
        List<FormInputResource> applicationFormInputs = this.setupApplicationFormInputs(QUESTION_ID, FORM_INPUT_TYPES.get("textarea"));
        this.setupApplicantResponses(APPLICATION_ID, applicationFormInputs);
        this.setupNextQuestionSection(sectionId, expectedNextQuestionId, true);

        MvcResult result = mockMvc.perform(post("/{assessmentId}/question/{questionId}", ASSESSMENT_ID, QUESTION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param(formInputScoreField, "10")
                .param(formInputFeedbackField, "Feedback"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form"))
                .andExpect(view().name("assessment/application-question"))
                .andReturn();

        verify(assessorFormInputResponseService, times(1)).updateFormInputResponse(ASSESSMENT_ID, formInputIdScore, "10");
        verify(assessorFormInputResponseService, times(1)).updateFormInputResponse(ASSESSMENT_ID, formInputIdFeedback, "Feedback");

        Form form = (Form) result.getModelAndView().getModel().get("form");

        assertEquals("10", form.getFormInput(formInputIdScore.toString()));
        assertEquals("Feedback", form.getFormInput(formInputIdFeedback.toString()));

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors(formInputFeedbackField));
        assertEquals("validation.field.max.word.count", bindingResult.getFieldError(formInputFeedbackField).getCode());
        assertEquals("100", bindingResult.getFieldError(formInputFeedbackField).getArguments()[1]);
    }

    @Override
    public void setupCompetition() {
        super.setupCompetition();

        competitionResource.setAssessorAcceptsDate(LocalDateTime.now().minusDays(2));
        competitionResource.setAssessorDeadlineDate(LocalDateTime.now().plusDays(4));

        questionResources.get(QUESTION_ID).setShortName("Market opportunity");
        questionResources.get(QUESTION_ID).setAssessorMaximumScore(50);
        questionResources.get(APPLICATION_DETAILS_QUESTION_ID).setShortName("Application details");
    }

    private void setupNextQuestionSection(Long sectionId, Long expectedNextQuestionId, boolean isAssessmentQuestion) {
        SectionResource section = newSectionResource().withDisplayInAssessmentApplicationSummary(isAssessmentQuestion).build();
        when(sectionService.getById(sectionId)).thenReturn(section);
        QuestionResource question = questionResources.get(expectedNextQuestionId);
        question.setSection(sectionId);
    }

    private List<FormInputResource> setupApplicationFormInputs(Long questionId, FormInputTypeResource... formInputTypes) {
        List<FormInputResource> formInputs = stream(formInputTypes).map(formInputType ->
                newFormInputResource()
                        .withFormInputType(formInputType.getId())
                        .withFormInputTypeTitle(formInputType.getTitle())
                        .build()
        ).collect(toList());
        when(formInputService.findApplicationInputsByQuestion(questionId)).thenReturn(formInputs);
        return formInputs;
    }

    private List<FormInputResource> setupAssessmentFormInputs(Long questionId, FormInputTypeResource... formInputTypes) {
        List<FormInputResource> formInputs = stream(formInputTypes).map(formInputType ->
                newFormInputResource()
                        .withFormInputType(formInputType.getId())
                        .withFormInputTypeTitle(formInputType.getTitle())
                        .build()
        ).collect(toList());
        when(formInputService.findAssessmentInputsByQuestion(questionId)).thenReturn(formInputs);
        return formInputs;
    }

    private List<FormInputResponseResource> setupApplicantResponses(Long applicationId, List<FormInputResource> formInputs) {
        List<FormInputResponseResource> applicantResponses = formInputs.stream().map(formInput ->
                newFormInputResponseResource()
                        .withFormInputs(formInput.getId())
                        .with(idBasedValues("Value "))
                        .build()
        ).collect(Collectors.toList());
        applicantResponses.forEach(formInputResponse -> when(formInputResponseService.getByFormInputIdAndApplication(formInputResponse.getFormInput(), applicationId)).thenReturn(restSuccess(asList(formInputResponse))));
        return applicantResponses;
    }

    private List<AssessorFormInputResponseResource> setupAssessorResponses(Long assessmentId, Long questionId, List<FormInputResource> formInputs) {
        List<AssessorFormInputResponseResource> assessorResponses = formInputs.stream().map(formInput ->
                newAssessorFormInputResponseResource()
                        .withFormInput(formInput.getId())
                        .withValue("Assessor Response")
                        .build()
        ).collect(toList());
        when(assessorFormInputResponseService.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId)).thenReturn(assessorResponses);
        return assessorResponses;
    }

    private AssessmentResource setupAssessment(Long applicationId) {
        AssessmentResource assessment = newAssessmentResource()
                .withId(1L)
                .withApplication(applicationId)
                .build();
        when(assessmentService.getById(assessment.getId())).thenReturn(assessment);
        return assessment;
    }
}