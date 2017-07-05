package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponsesResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static java.time.ZonedDateTime.now;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseBuilder.newAssessorFormInputResponse;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_SCORE;
import static org.innovateuk.ifs.form.resource.FormInputType.TEXTAREA;
import static org.innovateuk.ifs.util.CollectionFunctions.forEachWithIndex;
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
    public void updateFormInputResponses() throws Exception {
        Long assessmentId = 1L;
        ZonedDateTime oldUpdatedDate = now().minusHours(1);
        Long[] formInputIds = {1L, 2L};
        List<AssessorFormInputResponse> existingAssessorFormInputResponses = newAssessorFormInputResponse().build(2);
        List<AssessorFormInputResponseResource> existingAssessorFormInputResponseResources = newAssessorFormInputResponseResource()
                .withId(existingAssessorFormInputResponses.get(0).getId(), existingAssessorFormInputResponses.get(1).getId())
                .withAssessment(assessmentId)
                .withFormInput(formInputIds)
                .withValue("Existing response 1", "Existing response 2")
                .withUpdatedDate(oldUpdatedDate)
                .build(2);
        AssessorFormInputResponsesResource updatedAssessorFormInputResponsesResource = new AssessorFormInputResponsesResource(
                newAssessorFormInputResponseResource()
                        .withId(existingAssessorFormInputResponses.get(0).getId(), existingAssessorFormInputResponses.get(1).getId())
                        .withAssessment(assessmentId)
                        .withFormInput(formInputIds)
                        .withValue("New response 1", "New response 2")
                        .build(2));
        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(validationUtilMock.validateResponse(isA(FormInputResponse.class), eq(true))).thenReturn(bindingResult);

        forEachWithIndex(existingAssessorFormInputResponseResources, (index, responseResource) -> {
            long formInputId = responseResource.getFormInput();
            when(assessorFormInputResponseRepositoryMock.findByAssessmentIdAndFormInputId(assessmentId, formInputId))
                    .thenReturn(existingAssessorFormInputResponses.get(index));
            when(assessorFormInputResponseMapperMock.mapToResource(same(existingAssessorFormInputResponses.get(index))))
                    .thenReturn(responseResource);
        });

        when(assessmentWorkflowHandlerMock.feedback(assessment)).thenReturn(true);

        ServiceResult<Void> result = assessorFormInputResponseService.updateFormInputResponses(updatedAssessorFormInputResponsesResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessorFormInputResponseRepositoryMock, assessorFormInputResponseMapperMock,
                assessmentRepositoryMock, formInputRepositoryMock, validationUtilMock, assessmentWorkflowHandlerMock);

        updatedAssessorFormInputResponsesResource.getResponses().forEach(response -> {
            inOrder.verify(assessorFormInputResponseRepositoryMock).findByAssessmentIdAndFormInputId(assessmentId, response.getFormInput());
            inOrder.verify(assessorFormInputResponseMapperMock).mapToResource(isA(AssessorFormInputResponse.class));
            inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
            inOrder.verify(formInputRepositoryMock).findOne(response.getFormInput());
            inOrder.verify(validationUtilMock).validateResponse(isA(FormInputResponse.class), eq(true));
            inOrder.verify(assessorFormInputResponseMapperMock).mapToDomain(isA(AssessorFormInputResponseResource.class));
            inOrder.verify(assessmentWorkflowHandlerMock).feedback(assessment);
            inOrder.verify(assessorFormInputResponseRepositoryMock).save(createLambdaMatcher((AssessorFormInputResponse saved) -> {
                assertEquals(response.getId(), saved.getId());
                assertEquals(response.getAssessment(), saved.getAssessment().getId());
                assertEquals(response.getFormInput(), saved.getFormInput().getId());
                assertEquals(response.getValue(), saved.getValue());
                assertTrue(saved.getUpdatedDate().isAfter(oldUpdatedDate));
            }));
        });
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateFormInputResponses_notExists() throws Exception {
        Long assessmentId = 1L;
        Long[] formInputIds = {1L, 2L};
        AssessorFormInputResponsesResource updatedAssessorFormInputResponsesResource = new AssessorFormInputResponsesResource(
                newAssessorFormInputResponseResource()
                        .with(id(null))
                        .withAssessment(assessmentId)
                        .withFormInput(formInputIds)
                        .withValue("New response 1", "New response 2")
                        .build(2));
        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(validationUtilMock.validateResponse(isA(FormInputResponse.class), eq(true))).thenReturn(bindingResult);

        when(assessmentWorkflowHandlerMock.feedback(assessment)).thenReturn(true);

        ServiceResult<Void> result = assessorFormInputResponseService.updateFormInputResponses(updatedAssessorFormInputResponsesResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessorFormInputResponseRepositoryMock, assessorFormInputResponseMapperMock,
                assessmentRepositoryMock, formInputRepositoryMock, validationUtilMock, assessmentWorkflowHandlerMock);

        updatedAssessorFormInputResponsesResource.getResponses().forEach(response -> {
            inOrder.verify(assessorFormInputResponseRepositoryMock).findByAssessmentIdAndFormInputId(assessmentId, response.getFormInput());
            inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
            inOrder.verify(formInputRepositoryMock).findOne(response.getFormInput());
            inOrder.verify(validationUtilMock).validateResponse(isA(FormInputResponse.class), eq(true));
            inOrder.verify(assessorFormInputResponseMapperMock).mapToDomain(isA(AssessorFormInputResponseResource.class));
            inOrder.verify(assessmentWorkflowHandlerMock).feedback(assessment);
            inOrder.verify(assessorFormInputResponseRepositoryMock).save(createLambdaMatcher((AssessorFormInputResponse saved) -> {
                assertNull(saved.getId());
                assertEquals(response.getAssessment(), saved.getAssessment().getId());
                assertEquals(response.getFormInput(), saved.getFormInput().getId());
                assertEquals(response.getValue(), saved.getValue());
                assertNotNull(saved.getUpdatedDate());
            }));
        });
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateFormInputResponses_emptyOrNullValues() throws Exception {
        Long assessmentId = 1L;
        Long[] formInputIds = {1L, 2L};
        AssessorFormInputResponsesResource updatedAssessorFormInputResponsesResource = new AssessorFormInputResponsesResource(
                newAssessorFormInputResponseResource()
                        .with(id(null))
                        .withAssessment(assessmentId)
                        .withFormInput(formInputIds)
                        .withValue("", null)
                        .build(2));
        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(validationUtilMock.validateResponse(isA(FormInputResponse.class), eq(true))).thenReturn(bindingResult);

        when(assessmentWorkflowHandlerMock.feedback(assessment)).thenReturn(true);

        ServiceResult<Void> result = assessorFormInputResponseService.updateFormInputResponses(updatedAssessorFormInputResponsesResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessorFormInputResponseRepositoryMock, assessorFormInputResponseMapperMock,
                assessmentRepositoryMock, formInputRepositoryMock, validationUtilMock, assessmentWorkflowHandlerMock);

        updatedAssessorFormInputResponsesResource.getResponses().forEach(response -> {
            inOrder.verify(assessorFormInputResponseRepositoryMock).findByAssessmentIdAndFormInputId(assessmentId, response.getFormInput());
            inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
            inOrder.verify(formInputRepositoryMock).findOne(response.getFormInput());
            inOrder.verify(validationUtilMock).validateResponse(isA(FormInputResponse.class), eq(true));
            inOrder.verify(assessorFormInputResponseMapperMock).mapToDomain(isA(AssessorFormInputResponseResource.class));
            inOrder.verify(assessmentWorkflowHandlerMock).feedback(assessment);
            inOrder.verify(assessorFormInputResponseRepositoryMock).save(matchesWithNullValue(assessmentId, response.getFormInput()));
        });
        inOrder.verifyNoMoreInteractions();
    }

    private AssessorFormInputResponse matchesWithNullValue(Long assessmentId, Long formInputId) {
        return createLambdaMatcher((AssessorFormInputResponse saved) -> {
            assertNull(saved.getId());
            assertEquals(assessmentId, saved.getAssessment().getId());
            assertEquals(formInputId, saved.getFormInput().getId());
            // Make sure that the empty string value is reduced to null
            assertNull(saved.getValue());
            assertNotNull(saved.getUpdatedDate());
        });
    }

    @Test
    public void updateFormInputResponses_formInputValidatorError() throws Exception {
        Long assessmentId = 1L;
        Long[] formInputIds = {1L, 2L};
        AssessorFormInputResponsesResource updatedAssessorFormInputResponsesResource = new AssessorFormInputResponsesResource(
                newAssessorFormInputResponseResource()
                        .with(id(null))
                        .withAssessment(assessmentId)
                        .withFormInput(formInputIds)
                        .withValue("New response 1", "New response 2")
                        .build(2));
        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(singletonList(new FieldError(
                "objectName",
                "fieldName",
                "rejectedValue",
                false,
                null,
                new Object[]{"", 100},
                "validation.field.max.word.count")));
        when(validationUtilMock.validateResponse(isA(FormInputResponse.class), eq(true))).thenReturn(bindingResult);

        Error expectedError = fieldError("fieldName", "rejectedValue", "validation.field.max.word.count", "", 100);

        ServiceResult<Void> result = assessorFormInputResponseService.updateFormInputResponses(updatedAssessorFormInputResponsesResource);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(expectedError, expectedError));

        InOrder inOrder = inOrder(assessorFormInputResponseRepositoryMock, assessorFormInputResponseMapperMock,
                assessmentRepositoryMock, formInputRepositoryMock, validationUtilMock, assessmentWorkflowHandlerMock);

        updatedAssessorFormInputResponsesResource.getResponses().forEach(response -> {
            inOrder.verify(assessorFormInputResponseRepositoryMock).findByAssessmentIdAndFormInputId(assessmentId, response.getFormInput());
            inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
            inOrder.verify(formInputRepositoryMock).findOne(response.getFormInput());
            inOrder.verify(validationUtilMock).validateResponse(isA(FormInputResponse.class), eq(true));
        });
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateFormInputResponses_sameAsExistingValues() throws Exception {
        Long assessmentId = 1L;
        ZonedDateTime oldUpdatedDate = now().minusHours(1);
        Long[] formInputIds = {1L, 2L};
        List<AssessorFormInputResponse> existingAssessorFormInputResponses = newAssessorFormInputResponse().build(2);
        List<AssessorFormInputResponseResource> existingAssessorFormInputResponseResources = newAssessorFormInputResponseResource()
                .withId(existingAssessorFormInputResponses.get(0).getId(), existingAssessorFormInputResponses.get(1).getId())
                .withAssessment(assessmentId)
                .withFormInput(formInputIds)
                .withValue("Value that won't be touched 1", "Value that won't be touched 2")
                .withUpdatedDate(oldUpdatedDate)
                .build(2);
        AssessorFormInputResponsesResource updatedAssessorFormInputResponsesResource = new AssessorFormInputResponsesResource(
                newAssessorFormInputResponseResource()
                        .withId(existingAssessorFormInputResponses.get(0).getId(), existingAssessorFormInputResponses.get(1).getId())
                        .withAssessment(assessmentId)
                        .withFormInput(formInputIds)
                        .withValue("Value that won't be touched 1", "Value that won't be touched 2")
                        .build(2));
        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(validationUtilMock.validateResponse(isA(FormInputResponse.class), eq(true))).thenReturn(bindingResult);

        forEachWithIndex(existingAssessorFormInputResponseResources, (index, responseResource) -> {
            long formInputId = responseResource.getFormInput();
            when(assessorFormInputResponseRepositoryMock.findByAssessmentIdAndFormInputId(assessmentId, formInputId))
                    .thenReturn(existingAssessorFormInputResponses.get(index));
            when(assessorFormInputResponseMapperMock.mapToResource(same(existingAssessorFormInputResponses.get(index))))
                    .thenReturn(responseResource);
        });

        when(assessmentWorkflowHandlerMock.feedback(assessment)).thenReturn(true);

        ServiceResult<Void> result = assessorFormInputResponseService.updateFormInputResponses(updatedAssessorFormInputResponsesResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessorFormInputResponseRepositoryMock, assessorFormInputResponseMapperMock,
                assessmentRepositoryMock, formInputRepositoryMock, validationUtilMock, assessmentWorkflowHandlerMock);

        updatedAssessorFormInputResponsesResource.getResponses().forEach(response -> {
            inOrder.verify(assessorFormInputResponseRepositoryMock).findByAssessmentIdAndFormInputId(assessmentId, response.getFormInput());
            inOrder.verify(assessorFormInputResponseMapperMock).mapToResource(isA(AssessorFormInputResponse.class));
            inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
            inOrder.verify(formInputRepositoryMock).findOne(response.getFormInput());
            inOrder.verify(validationUtilMock).validateResponse(isA(FormInputResponse.class), eq(true));
            inOrder.verify(assessorFormInputResponseMapperMock).mapToDomain(isA(AssessorFormInputResponseResource.class));
            inOrder.verify(assessmentWorkflowHandlerMock).feedback(assessment);
            inOrder.verify(assessorFormInputResponseRepositoryMock).save(createLambdaMatcher((AssessorFormInputResponse saved) -> {
                assertEquals(response.getId(), saved.getId());
                assertEquals(response.getAssessment(), saved.getAssessment().getId());
                assertEquals(response.getFormInput(), saved.getFormInput().getId());
                assertEquals(response.getValue(), saved.getValue());

                // The updated date should not have been touched since the value was the same
                assertEquals(oldUpdatedDate, saved.getUpdatedDate());
            }));
        });
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void updateFormInputResponses_transitionNotAccepted() throws Exception {
        Long assessmentId = 1L;
        Long[] formInputIds = {1L, 2L};
        AssessorFormInputResponsesResource updatedAssessorFormInputResponsesResource = new AssessorFormInputResponsesResource(
                newAssessorFormInputResponseResource()
                        .with(id(null))
                        .withAssessment(assessmentId)
                        .withFormInput(formInputIds)
                        .withValue("New response 1", "New response 2")
                        .build(2));
        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(validationUtilMock.validateResponse(isA(FormInputResponse.class), eq(true))).thenReturn(bindingResult);

        when(assessmentWorkflowHandlerMock.feedback(assessment)).thenReturn(false);

        ServiceResult<Void> result = assessorFormInputResponseService.updateFormInputResponses(updatedAssessorFormInputResponsesResource);
        assertTrue(result.isFailure());

        InOrder inOrder = inOrder(assessorFormInputResponseRepositoryMock, assessorFormInputResponseMapperMock,
                assessmentRepositoryMock, formInputRepositoryMock, validationUtilMock, assessmentWorkflowHandlerMock);

        updatedAssessorFormInputResponsesResource.getResponses().forEach(response -> {
            inOrder.verify(assessorFormInputResponseRepositoryMock).findByAssessmentIdAndFormInputId(assessmentId, response.getFormInput());
            inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
            inOrder.verify(formInputRepositoryMock).findOne(response.getFormInput());
            inOrder.verify(validationUtilMock).validateResponse(isA(FormInputResponse.class), eq(true));
            inOrder.verify(assessorFormInputResponseMapperMock).mapToDomain(isA(AssessorFormInputResponseResource.class));
            inOrder.verify(assessmentWorkflowHandlerMock).feedback(assessment);
        });
        inOrder.verifyNoMoreInteractions();
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

        assertTrue(scores.isScopeAssessed());
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
    public void getApplicationAggregateScores_scopeNotAssessed() {
        long applicationId = 7;

        List<FormInput> scoreFormInputs = newFormInput()
                .withType(ASSESSOR_SCORE)
                .withQuestion(newQuestion().withId(1L, 2L).withAssessorMaximumScore(5, 10).buildArray(2, Question.class))
                .build(2);

        List<AssessorFormInputResponse> assessorFormInputResponses = newAssessorFormInputResponse()
                .withFormInput(scoreFormInputs.get(0), scoreFormInputs.get(0), scoreFormInputs.get(0),
                        scoreFormInputs.get(1), scoreFormInputs.get(1), scoreFormInputs.get(1))
                .withValue("1", "2", "3", "4", "6", "7")
                .build(6);

        when(assessorFormInputResponseRepositoryMock.findByAssessmentTargetId(applicationId)).thenReturn(assessorFormInputResponses);

        ApplicationAssessmentAggregateResource scores = assessorFormInputResponseService.getApplicationAggregateScores(applicationId).getSuccessObjectOrThrowException();

        assertFalse(scores.isScopeAssessed());
        assertEquals(0, scores.getTotalScope());
        assertEquals(0, scores.getInScope());
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
