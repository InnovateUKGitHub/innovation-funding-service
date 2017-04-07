package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static java.time.ZonedDateTime.now;
import static java.util.Collections.nCopies;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseBuilder.newAssessorFormInputResponse;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class AssessorFormInputResponseServiceImplTest extends BaseUnitTestMocksTest {
    @InjectMocks
    private AssessorFormInputResponseService assessorFormInputResponseService = new AssessorFormInputResponseServiceImpl();

    @Before
    public void setUp() throws Exception {
        when(assessorFormInputResponseMapperMock.mapToDomain(any(AssessorFormInputResponseResource.class))).thenAnswer(invocation -> {
            AssessorFormInputResponseResource assessorFormInputResponseResource = invocation.getArgumentAt(0, AssessorFormInputResponseResource.class);
            return newAssessorFormInputResponse()
                    .with(id(assessorFormInputResponseResource.getId()))
                    .withAssessment(newAssessment().with(id(assessorFormInputResponseResource.getAssessment())).build())
                    .withFormInput(newFormInput().with(id(assessorFormInputResponseResource.getFormInput())).build())
                    .withValue(assessorFormInputResponseResource.getValue())
                    .withUpdatedDate(assessorFormInputResponseResource.getUpdatedDate())
                    .build();
        });
    }

    @Test
    public void getAllAssessorFormInputResponses() throws Exception {
        List<AssessorFormInputResponse> assessorFormInputResponses = newAssessorFormInputResponse().build(2);

        List<AssessorFormInputResponseResource> assessorFormInputResponseResources = newAssessorFormInputResponseResource().build(2);

        Long assessmentId = 1L;

        when(assessorFormInputResponseRepositoryMock.findByAssessmentId(assessmentId)).thenReturn(assessorFormInputResponses);
        when(assessorFormInputResponseMapperMock.mapToResource(same(assessorFormInputResponses.get(0)))).thenReturn(assessorFormInputResponseResources.get(0));
        when(assessorFormInputResponseMapperMock.mapToResource(same(assessorFormInputResponses.get(1)))).thenReturn(assessorFormInputResponseResources.get(1));

        List<AssessorFormInputResponseResource> found = assessorFormInputResponseService.getAllAssessorFormInputResponses(assessmentId).getSuccessObject();

        assertEquals(assessorFormInputResponseResources, found);
        verify(assessorFormInputResponseRepositoryMock, only()).findByAssessmentId(assessmentId);
    }

    @Test
    public void getAllAssessorFormInputResponsesByAssessmentAndQuestion() throws Exception {
        List<AssessorFormInputResponse> assessorFormInputResponses = newAssessorFormInputResponse().build(2);

        List<AssessorFormInputResponseResource> assessorFormInputResponseResources = newAssessorFormInputResponseResource().build(2);

        Long assessmentId = 1L;
        Long questionId = 2L;

        when(assessorFormInputResponseRepositoryMock.findByAssessmentIdAndFormInputQuestionId(assessmentId, questionId)).thenReturn(assessorFormInputResponses);
        when(assessorFormInputResponseMapperMock.mapToResource(same(assessorFormInputResponses.get(0)))).thenReturn(assessorFormInputResponseResources.get(0));
        when(assessorFormInputResponseMapperMock.mapToResource(same(assessorFormInputResponses.get(1)))).thenReturn(assessorFormInputResponseResources.get(1));

        List<AssessorFormInputResponseResource> found = assessorFormInputResponseService.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId).getSuccessObject();

        assertEquals(assessorFormInputResponseResources, found);
        verify(assessorFormInputResponseRepositoryMock, only()).findByAssessmentIdAndFormInputQuestionId(assessmentId, questionId);
    }

    @Test
    public void updateFormInputResponse() throws Exception {
        Long assessmentId = 1L;
        Long formInputId = 2L;
        String value = "New feedback";
        String oldValue = "Old feedback";
        ZonedDateTime oldUpdatedDate = now().minusHours(1);
        AssessorFormInputResponse existingAssessorFormInputResponse = newAssessorFormInputResponse().build();
        AssessorFormInputResponseResource existingAssessorFormInputResponseResource = newAssessorFormInputResponseResource()
                .withId(3L)
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(oldValue)
                .withUpdatedDate(oldUpdatedDate)
                .build();
        AssessorFormInputResponseResource updatedAssessorFormInputResponseResource = newAssessorFormInputResponseResource()
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(value)
                .build();
        FormInputResource formInput = newFormInputResource()
                .withId(formInputId)
                .withWordCount(10)
                .build();
        Assessment assessment = newAssessment().build();
        BindingResult bindingResult = new DataBinder(updatedAssessorFormInputResponseResource).getBindingResult();

        ArgumentCaptor<Assessment> assessmentWorkflowHandlerFeedbackArgument = ArgumentCaptor.forClass(Assessment.class);
        ArgumentCaptor<AssessorFormInputResponse> formInputResponseSaveArgument = ArgumentCaptor.forClass(AssessorFormInputResponse.class);

        when(validationUtilMock.validateResponse(any(FormInputResponse.class), anyBoolean())).thenReturn(bindingResult);
        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(formInputServiceMock.findFormInput(formInputId)).thenReturn(serviceSuccess(formInput));
        when(assessorFormInputResponseRepositoryMock.findByAssessmentIdAndFormInputId(assessmentId, formInputId)).thenReturn(existingAssessorFormInputResponse);
        when(assessorFormInputResponseMapperMock.mapToResource(same(existingAssessorFormInputResponse))).thenReturn(existingAssessorFormInputResponseResource);

        ServiceResult<Void> result = assessorFormInputResponseService.updateFormInputResponse(updatedAssessorFormInputResponseResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessorFormInputResponseRepositoryMock, assessorFormInputResponseMapperMock, assessmentWorkflowHandlerMock);
        inOrder.verify(assessorFormInputResponseRepositoryMock).findByAssessmentIdAndFormInputId(assessmentId, formInputId);
        inOrder.verify(assessorFormInputResponseMapperMock).mapToResource(same(existingAssessorFormInputResponse));
        inOrder.verify(assessorFormInputResponseMapperMock).mapToDomain(same(existingAssessorFormInputResponseResource));
        inOrder.verify(assessorFormInputResponseRepositoryMock).save(formInputResponseSaveArgument.capture());
        inOrder.verify(assessmentWorkflowHandlerMock).feedback(assessmentWorkflowHandlerFeedbackArgument.capture());
        inOrder.verifyNoMoreInteractions();

        assertEquals(assessmentId, assessmentWorkflowHandlerFeedbackArgument.getValue().getId());
        AssessorFormInputResponse saved = formInputResponseSaveArgument.getValue();
        assertEquals(existingAssessorFormInputResponseResource.getId(), saved.getId());
        assertEquals(existingAssessorFormInputResponseResource.getAssessment(), saved.getAssessment().getId());
        assertEquals(existingAssessorFormInputResponseResource.getFormInput(), saved.getFormInput().getId());
        assertEquals(value, saved.getValue());
        assertTrue(saved.getUpdatedDate().isAfter(oldUpdatedDate));
    }

    @Test
    public void updateFormInputResponse_notExists() throws Exception {
        Long assessmentId = 1L;
        Long formInputId = 2L;
        String value = "New feedback";

        AssessorFormInputResponseResource assessorFormInputResponseResource = newAssessorFormInputResponseResource()
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(value)
                .build();
        FormInputResource formInput = newFormInputResource()
                .withId(formInputId)
                .withWordCount(10)
                .build();
        Assessment assessment = newAssessment().build();
        BindingResult bindingResult = new DataBinder(assessorFormInputResponseResource).getBindingResult();

        ArgumentCaptor<Assessment> assessmentWorkflowHandlerFeedbackArgument = ArgumentCaptor.forClass(Assessment.class);
        ArgumentCaptor<AssessorFormInputResponse> formInputResponseSaveArgument = ArgumentCaptor.forClass(AssessorFormInputResponse.class);

        when(validationUtilMock.validateResponse(any(FormInputResponse.class), anyBoolean())).thenReturn(bindingResult);
        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(formInputServiceMock.findFormInput(formInputId)).thenReturn(serviceSuccess(formInput));

        ServiceResult<Void> result = assessorFormInputResponseService.updateFormInputResponse(assessorFormInputResponseResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessorFormInputResponseRepositoryMock, assessorFormInputResponseMapperMock, assessmentWorkflowHandlerMock);
        inOrder.verify(assessorFormInputResponseRepositoryMock).findByAssessmentIdAndFormInputId(assessmentId, formInputId);
        inOrder.verify(assessorFormInputResponseMapperMock).mapToDomain(isA(AssessorFormInputResponseResource.class));
        inOrder.verify(assessorFormInputResponseRepositoryMock).save(formInputResponseSaveArgument.capture());
        inOrder.verify(assessmentWorkflowHandlerMock).feedback(assessmentWorkflowHandlerFeedbackArgument.capture());
        inOrder.verifyNoMoreInteractions();

        assertEquals(assessmentId, assessmentWorkflowHandlerFeedbackArgument.getValue().getId());
        AssessorFormInputResponse saved = formInputResponseSaveArgument.getValue();
        assertNull(saved.getId());
        assertEquals(assessmentId, saved.getAssessment().getId());
        assertEquals(formInputId, saved.getFormInput().getId());
        assertEquals(value, saved.getValue());
        assertNotNull(saved.getUpdatedDate());
    }

    @Test
    public void updateFormInputResponse_nullValue() throws Exception {
        Long assessmentId = 1L;
        Long formInputId = 2L;
        String value = null;

        AssessorFormInputResponseResource assessorFormInputResponseResource = newAssessorFormInputResponseResource()
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(value)
                .build();
        FormInputResource formInput = newFormInputResource()
                .withId(formInputId)
                .withWordCount(10)
                .build();
        Assessment assessment = newAssessment().build();
        BindingResult bindingResult = new DataBinder(assessorFormInputResponseResource).getBindingResult();

        ArgumentCaptor<Assessment> assessmentWorkflowHandlerFeedbackArgument = ArgumentCaptor.forClass(Assessment.class);
        ArgumentCaptor<AssessorFormInputResponse> formInputResponseSaveArgument = ArgumentCaptor.forClass(AssessorFormInputResponse.class);

        when(validationUtilMock.validateResponse(any(FormInputResponse.class), anyBoolean())).thenReturn(bindingResult);
        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(formInputServiceMock.findFormInput(formInputId)).thenReturn(serviceSuccess(formInput));

        ServiceResult<Void> result = assessorFormInputResponseService.updateFormInputResponse(assessorFormInputResponseResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessorFormInputResponseRepositoryMock, assessorFormInputResponseMapperMock, assessmentWorkflowHandlerMock);
        inOrder.verify(assessorFormInputResponseRepositoryMock).findByAssessmentIdAndFormInputId(assessmentId, formInputId);
        inOrder.verify(assessorFormInputResponseMapperMock).mapToDomain(isA(AssessorFormInputResponseResource.class));
        inOrder.verify(assessorFormInputResponseRepositoryMock).save(formInputResponseSaveArgument.capture());
        inOrder.verify(assessmentWorkflowHandlerMock).feedback(assessmentWorkflowHandlerFeedbackArgument.capture());
        inOrder.verifyNoMoreInteractions();

        assertEquals(assessmentId, assessmentWorkflowHandlerFeedbackArgument.getValue().getId());
        AssessorFormInputResponse saved = formInputResponseSaveArgument.getValue();
        assertNull(saved.getId());
        assertEquals(assessmentId, saved.getAssessment().getId());
        assertEquals(formInputId, saved.getFormInput().getId());
        assertNull(saved.getValue());
        assertNotNull(saved.getUpdatedDate());
    }

    @Test
    public void updateFormInputResponse_emptyValue() throws Exception {
        Long assessmentId = 1L;
        Long formInputId = 2L;
        String value = "";

        AssessorFormInputResponseResource assessorFormInputResponseResource = newAssessorFormInputResponseResource()
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(value)
                .build();
        FormInputResource formInput = newFormInputResource()
                .withId(formInputId)
                .withWordCount(10)
                .build();
        Assessment assessment = newAssessment().build();
        BindingResult bindingResult = new DataBinder(assessorFormInputResponseResource).getBindingResult();

        ArgumentCaptor<Assessment> assessmentWorkflowHandlerFeedbackArgument = ArgumentCaptor.forClass(Assessment.class);
        ArgumentCaptor<AssessorFormInputResponse> formInputResponseSaveArgument = ArgumentCaptor.forClass(AssessorFormInputResponse.class);

        when(validationUtilMock.validateResponse(any(FormInputResponse.class), anyBoolean())).thenReturn(bindingResult);
        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(formInputServiceMock.findFormInput(formInputId)).thenReturn(serviceSuccess(formInput));

        ServiceResult<Void> result = assessorFormInputResponseService.updateFormInputResponse(assessorFormInputResponseResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessorFormInputResponseRepositoryMock, assessorFormInputResponseMapperMock, assessmentWorkflowHandlerMock);
        inOrder.verify(assessorFormInputResponseRepositoryMock).findByAssessmentIdAndFormInputId(assessmentId, formInputId);
        inOrder.verify(assessorFormInputResponseMapperMock).mapToDomain(isA(AssessorFormInputResponseResource.class));
        inOrder.verify(assessorFormInputResponseRepositoryMock).save(formInputResponseSaveArgument.capture());
        inOrder.verify(assessmentWorkflowHandlerMock).feedback(assessmentWorkflowHandlerFeedbackArgument.capture());
        inOrder.verifyNoMoreInteractions();

        assertEquals(assessmentId, assessmentWorkflowHandlerFeedbackArgument.getValue().getId());
        AssessorFormInputResponse saved = formInputResponseSaveArgument.getValue();
        assertNull(saved.getId());
        assertEquals(assessmentId, saved.getAssessment().getId());
        assertEquals(formInputId, saved.getFormInput().getId());
        // Make sure that the empty string value is reduced to null
        assertNull(saved.getValue());
        assertNotNull(saved.getUpdatedDate());
    }

    @Test
    public void updateFormInputResponse_exceedsWordLimit() throws Exception {
        Long assessmentId = 1L;
        Long formInputId = 2L;
        String value = String.join(" ", nCopies(101, "response"));

        AssessorFormInputResponseResource assessorFormInputResponseResource = newAssessorFormInputResponseResource()
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(value)
                .build();
        FormInputResource formInput = newFormInputResource()
                .withId(formInputId)
                .withWordCount(100)
                .build();
        Assessment assessment = newAssessment().build();
        BindingResult bindingResult = new DataBinder(assessorFormInputResponseResource).getBindingResult();
        bindingResult.addError(new FieldError(
                "value",
                "value",
                value,
                false,
                null,
                singletonList((Object) 100).toArray(),
                "validation.field.max.word.count"));

        when(validationUtilMock.validateResponse(any(), anyBoolean())).thenReturn(bindingResult);
        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(formInputServiceMock.findFormInput(formInputId)).thenReturn(serviceSuccess(formInput));

        ServiceResult<Void> result = assessorFormInputResponseService.updateFormInputResponse(assessorFormInputResponseResource);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(fieldError("value", value, "validation.field.max.word.count", 100)));
    }

    @Test
    public void updateFormInputResponse_noWordLimit() throws Exception {
        Long assessmentId = 1L;
        Long formInputId = 2L;
        String value = "This is the feedback text";

        AssessorFormInputResponseResource assessorFormInputResponseResource = newAssessorFormInputResponseResource()
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(value)
                .build();
        FormInputResource formInput = newFormInputResource()
                .withId(formInputId)
                .build();
        Assessment assessment = newAssessment().build();
        BindingResult bindingResult = new DataBinder(assessorFormInputResponseResource).getBindingResult();

        when(validationUtilMock.validateResponse(any(FormInputResponse.class), anyBoolean())).thenReturn(bindingResult);
        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(formInputServiceMock.findFormInput(formInputId)).thenReturn(serviceSuccess(formInput));

        ServiceResult<Void> result = assessorFormInputResponseService.updateFormInputResponse(assessorFormInputResponseResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void updateFormInputResponse_sameAsExistingValue() throws Exception {
        Long assessmentId = 1L;
        Long formInputId = 2L;
        String value = "Value that won't be touched";
        String oldValue = "Value that won't be touched";
        ZonedDateTime oldUpdatedDate = now().minusHours(1);
        AssessorFormInputResponse existingAssessorFormInputResponse = newAssessorFormInputResponse().build();
        AssessorFormInputResponseResource existingAssessorFormInputResponseResource = newAssessorFormInputResponseResource()
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(oldValue)
                .withUpdatedDate(oldUpdatedDate)
                .build();
        AssessorFormInputResponseResource assessorFormInputResponseResource = newAssessorFormInputResponseResource()
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(oldValue)
                .build();

        FormInputResource formInput = newFormInputResource()
                .withId(formInputId)
                .withWordCount(10)
                .build();
        Assessment assessment = newAssessment().build();
        BindingResult bindingResult = new DataBinder(assessorFormInputResponseResource).getBindingResult();

        ArgumentCaptor<Assessment> assessmentWorkflowHandlerFeedbackArgument = ArgumentCaptor.forClass(Assessment.class);
        ArgumentCaptor<AssessorFormInputResponse> formInputResponseSaveArgument = ArgumentCaptor.forClass(AssessorFormInputResponse.class);

        when(validationUtilMock.validateResponse(any(FormInputResponse.class), anyBoolean())).thenReturn(bindingResult);
        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(formInputServiceMock.findFormInput(formInputId)).thenReturn(serviceSuccess(formInput));

        when(assessorFormInputResponseRepositoryMock.findByAssessmentIdAndFormInputId(assessmentId, formInputId)).thenReturn(existingAssessorFormInputResponse);
        when(assessorFormInputResponseMapperMock.mapToResource(same(existingAssessorFormInputResponse))).thenReturn(existingAssessorFormInputResponseResource);

        ServiceResult<Void> result = assessorFormInputResponseService.updateFormInputResponse(assessorFormInputResponseResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessorFormInputResponseRepositoryMock, assessorFormInputResponseMapperMock, assessmentWorkflowHandlerMock);
        inOrder.verify(assessorFormInputResponseRepositoryMock).findByAssessmentIdAndFormInputId(assessmentId, formInputId);
        inOrder.verify(assessorFormInputResponseMapperMock).mapToResource(same(existingAssessorFormInputResponse));
        inOrder.verify(assessorFormInputResponseMapperMock).mapToDomain(same(existingAssessorFormInputResponseResource));
        inOrder.verify(assessorFormInputResponseRepositoryMock).save(formInputResponseSaveArgument.capture());
        inOrder.verify(assessmentWorkflowHandlerMock).feedback(assessmentWorkflowHandlerFeedbackArgument.capture());
        inOrder.verifyNoMoreInteractions();

        assertEquals(assessmentId, assessmentWorkflowHandlerFeedbackArgument.getValue().getId());
        AssessorFormInputResponse saved = formInputResponseSaveArgument.getValue();
        assertEquals(existingAssessorFormInputResponseResource.getId(), saved.getId());
        assertEquals(existingAssessorFormInputResponseResource.getAssessment(), saved.getAssessment().getId());
        assertEquals(existingAssessorFormInputResponseResource.getFormInput(), saved.getFormInput().getId());
        assertEquals(value, saved.getValue());

        // The updated date should not have been touched since the value was the same
        assertEquals(oldUpdatedDate, saved.getUpdatedDate());
    }

    @Test
    public void updateFormInputResponse_badCategory() throws Exception {
        Long assessmentId = 1L;
        Long formInputId = 2L;
        String value = "1";
        String oldValue = "1";
        ZonedDateTime oldUpdatedDate = now().minusHours(1);
        AssessorFormInputResponse existingAssessorFormInputResponse = newAssessorFormInputResponse().build();
        AssessorFormInputResponseResource existingAssessorFormInputResponseResource = newAssessorFormInputResponseResource()
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(oldValue)
                .withUpdatedDate(oldUpdatedDate)
                .build();
        AssessorFormInputResponseResource updatedAssessorFormInputResponseResource = newAssessorFormInputResponseResource()
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(value)
                .build();
        FormInputResource formInput = newFormInputResource()
                .withId(formInputId)
                .withWordCount(0)
                .withType(ASSESSOR_RESEARCH_CATEGORY)
                .build();

        ResearchCategoryResource categoryResource = newResearchCategoryResource()
                .withId(2L)
                .withName("name")
                .build();

        Assessment assessment = newAssessment().build();
        BindingResult bindingResult = new DataBinder(updatedAssessorFormInputResponseResource).getBindingResult();
        bindingResult.addError(new FieldError(
                "value",
                "value",
                value,
                false,
                null,
                null,
                "validation.assessor.category.invalidCategory"));

        when(validationUtilMock.validateResponse(any(FormInputResponse.class), anyBoolean())).thenReturn(bindingResult);
        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(formInputServiceMock.findFormInput(formInputId)).thenReturn(serviceSuccess(formInput));
        when(categoryServiceMock.getResearchCategories()).thenReturn(serviceSuccess(singletonList(categoryResource)));
        when(assessorFormInputResponseRepositoryMock.findByAssessmentIdAndFormInputId(assessmentId, formInputId)).thenReturn(existingAssessorFormInputResponse);
        when(assessorFormInputResponseMapperMock.mapToResource(same(existingAssessorFormInputResponse))).thenReturn(existingAssessorFormInputResponseResource);

        ServiceResult<Void> result = assessorFormInputResponseService.updateFormInputResponse(updatedAssessorFormInputResponseResource);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(fieldError("value", value, "validation.assessor.category.invalidCategory")));
    }


    @Test
    public void getApplicationAggregateScores() {
        long applicationId = 7;

        FormInput scopeFormInput = newFormInput()
                .withType(FormInputType.ASSESSOR_APPLICATION_IN_SCOPE)
                .build();
        FormInput otherFormInput = newFormInput()
                .withType(FormInputType.ASSESSOR_RESEARCH_CATEGORY)
                .build();

        List<AssessorFormInputResponse> assessorFormInputResponses = newAssessorFormInputResponse()
                .withFormInput(scopeFormInput, otherFormInput, scopeFormInput)
                .withValue("true", "true", "false")
                .build(3);

        List<FormInput> scoreFormInputs = newFormInput()
                .withType(ASSESSOR_SCORE)
                .withQuestion(newQuestion().withId(1L, 2L).withAssessorMaximumScore(5, 10).buildArray(2, Question.class))
                .build(2);

        List<AssessorFormInputResponse> assessorFormInputScores = newAssessorFormInputResponse()
                .withFormInput(scoreFormInputs.get(0), scoreFormInputs.get(0), scoreFormInputs.get(0),
                        scoreFormInputs.get(1), scoreFormInputs.get(1), scoreFormInputs.get(1))
                .withValue("1", "2", "3", "4", "6", "7")
                .build(6);

        assessorFormInputResponses.addAll(assessorFormInputScores);

        when(assessorFormInputResponseRepositoryMock.findByAssessmentTargetId(applicationId)).thenReturn(assessorFormInputResponses);

        ApplicationAssessmentAggregateResource scores = assessorFormInputResponseService.getApplicationAggregateScores(applicationId).getSuccessObjectOrThrowException();

        assertEquals(2, scores.getTotalScope());
        assertEquals(1, scores.getInScope());
        assertEquals(2, scores.getScores().keySet().size());
        assertTrue(scores.getScores().containsKey(1L));
        assertTrue(new BigDecimal("2").equals(scores.getScores().get(1L)));
        assertTrue(scores.getScores().containsKey(2L));
        assertTrue(new BigDecimal("6").equals(scores.getScores().get(2L)));
        assertEquals(48L, scores.getAveragePercentage());
    }

    @Test
    public void getAssessmentAggregateFeedback() {
        long applicationId = 1L;
        long questionId = 2L;

        List<FormInput> scoreFormInputs = newFormInput()
                .withType(ASSESSOR_SCORE)
                .build(2);

        List<FormInput> feedbackFormInputs = newFormInput()
                .withType(TEXTAREA)
                .build(2);

        FormInput[] formInputs = newFormInput()
                .withType(ASSESSOR_SCORE, TEXTAREA, ASSESSOR_SCORE, TEXTAREA)
                .buildArray(4, FormInput.class);

        List<AssessorFormInputResponse> assessorFormInputResponses = newAssessorFormInputResponse()
                .withFormInput(formInputs)
                .withValue("2", "Feedback 1", "4", "Feedback 2")
                .build(4);

        when(assessorFormInputResponseRepositoryMock.findByAssessmentTargetIdAndFormInputQuestionId(applicationId, questionId)).thenReturn(assessorFormInputResponses);

        AssessmentFeedbackAggregateResource feedback = assessorFormInputResponseService.getAssessmentAggregateFeedback(applicationId, questionId).getSuccessObjectOrThrowException();

        assertEquals(new BigDecimal("3"), feedback.getAvgScore());
        assertEquals(2, feedback.getFeedback().size());
        assertEquals("Feedback 1", feedback.getFeedback().get(0));
        assertEquals("Feedback 2", feedback.getFeedback().get(1));
    }
}
