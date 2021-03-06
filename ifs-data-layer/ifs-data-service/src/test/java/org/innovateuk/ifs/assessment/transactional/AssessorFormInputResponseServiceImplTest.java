package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.validation.ApplicationValidationUtil;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentFundingDecisionOutcome;
import org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse;
import org.innovateuk.ifs.assessment.mapper.AssessorFormInputResponseMapper;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.repository.AssessorFormInputResponseRepository;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static java.time.ZonedDateTime.now;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeBuilder.newAssessmentFundingDecisionOutcome;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseBuilder.newAssessorFormInputResponse;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.forEachWithIndex;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

public class AssessorFormInputResponseServiceImplTest extends BaseUnitTestMocksTest {

    @Mock
    private AssessorFormInputResponseRepository assessorFormInputResponseRepositoryMock;

    @Mock
    private AssessorFormInputResponseMapper assessorFormInputResponseMapperMock;

    @Mock
    private AssessmentRepository assessmentRepositoryMock;

    @Mock
    private ApplicationValidationUtil validationUtilMock;

    @Mock
    private AssessmentWorkflowHandler assessmentWorkflowHandlerMock;

    @Mock
    private FormInputRepository formInputRepositoryMock;

    @InjectMocks
    private AssessorFormInputResponseService assessorFormInputResponseService = new AssessorFormInputResponseServiceImpl();

    @Before
    public void setUp() throws Exception {
        when(assessorFormInputResponseMapperMock.mapToDomain(any(AssessorFormInputResponseResource.class))).thenAnswer(invocation -> {
            AssessorFormInputResponseResource assessorFormInputResponseResource = invocation.getArgument(0);
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

        List<AssessorFormInputResponseResource> found = assessorFormInputResponseService.getAllAssessorFormInputResponses(assessmentId).getSuccess();

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

        List<AssessorFormInputResponseResource> found = assessorFormInputResponseService.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId).getSuccess();

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

        when(assessmentRepositoryMock.findById(assessmentId)).thenReturn(Optional.of(assessment));

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
            inOrder.verify(assessmentRepositoryMock).findById(assessmentId);
            inOrder.verify(formInputRepositoryMock).findById(response.getFormInput());
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

        when(assessmentRepositoryMock.findById(assessmentId)).thenReturn(Optional.of(assessment));

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
            inOrder.verify(assessmentRepositoryMock).findById(assessmentId);
            inOrder.verify(formInputRepositoryMock).findById(response.getFormInput());
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

        when(assessmentRepositoryMock.findById(assessmentId)).thenReturn(Optional.of(assessment));

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
            inOrder.verify(assessmentRepositoryMock).findById(assessmentId);
            inOrder.verify(formInputRepositoryMock).findById(response.getFormInput());
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

        when(assessmentRepositoryMock.findById(assessmentId)).thenReturn(Optional.of(assessment));

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
            inOrder.verify(assessmentRepositoryMock).findById(assessmentId);
            inOrder.verify(formInputRepositoryMock).findById(response.getFormInput());
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

        when(assessmentRepositoryMock.findById(assessmentId)).thenReturn(Optional.of(assessment));

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
            inOrder.verify(assessmentRepositoryMock).findById(assessmentId);
            inOrder.verify(formInputRepositoryMock).findById(response.getFormInput());
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

        when(assessmentRepositoryMock.findById(assessmentId)).thenReturn(Optional.of(assessment));

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
            inOrder.verify(assessmentRepositoryMock).findById(assessmentId);
            inOrder.verify(formInputRepositoryMock).findById(response.getFormInput());
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

        ApplicationAssessmentAggregateResource scores = assessorFormInputResponseService.getApplicationAggregateScores(applicationId).getSuccess();

        assertTrue(scores.isScopeAssessed());
        assertEquals(2, scores.getTotalScope());
        assertEquals(1, scores.getInScope());
        assertEquals(2, scores.getScores().keySet().size());
        assertTrue(scores.getScores().containsKey(1L));
        assertTrue(new BigDecimal("2.0").equals(scores.getScores().get(1L)));
        assertTrue(scores.getScores().containsKey(2L));
        assertTrue(new BigDecimal("5.7").equals(scores.getScores().get(2L)));
        assertEquals(new BigDecimal("48.3"), scores.getAveragePercentage());
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

        ApplicationAssessmentAggregateResource scores = assessorFormInputResponseService.getApplicationAggregateScores(applicationId).getSuccess();

        assertFalse(scores.isScopeAssessed());
        assertEquals(0, scores.getTotalScope());
        assertEquals(0, scores.getInScope());
        assertEquals(2, scores.getScores().keySet().size());
        assertTrue(scores.getScores().containsKey(1L));
        assertTrue(new BigDecimal("2.0").equals(scores.getScores().get(1L)));
        assertTrue(scores.getScores().containsKey(2L));
        assertTrue(new BigDecimal("5.7").equals(scores.getScores().get(2L)));
        assertEquals(new BigDecimal("48.3"), scores.getAveragePercentage());
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

        AssessmentFeedbackAggregateResource feedback = assessorFormInputResponseService.getAssessmentAggregateFeedback(applicationId, questionId).getSuccess();

        assertEquals(new BigDecimal("3.0"), feedback.getAvgScore());
        assertEquals(2, feedback.getFeedback().size());
        assertEquals("Feedback 1", feedback.getFeedback().get(0));
        assertEquals("Feedback 2", feedback.getFeedback().get(1));
    }

    @Test
    public void getApplicationAssessment() {
        long applicationId = 1L;
        long assessmentId = 2L;

        List<FormInput> scoreFormInputs = newFormInput()
                .withType(ASSESSOR_SCORE)
                .withQuestion(newQuestion().withId(1L, 2L).withAssessorMaximumScore(5, 10).buildArray(2, Question.class))
                .build(2);

        List<FormInput> feedbackFormInputs = newFormInput()
                .withType(TEXTAREA)
                .withQuestion(newQuestion().withId(1L, 2L).buildArray(2, Question.class))
                .build(2);

        FormInput scopeInput = newFormInput()
                .withType(ASSESSOR_APPLICATION_IN_SCOPE)
                .build();

        List<AssessorFormInputResponse> assessorFormInputResponses = newAssessorFormInputResponse()
                .withFormInput(scoreFormInputs.get(0), scoreFormInputs.get(1), feedbackFormInputs.get(0), feedbackFormInputs.get(1), scopeInput)
                .withValue("5", "9", "Feedback 1","Feedback 2", "true")
                .build(5);

        String expectedFundingFeedback = "Feedback";

        AssessmentFundingDecisionOutcome assessmentFundingDecisionOutcome = newAssessmentFundingDecisionOutcome()
                .withFundingConfirmation(true)
                .withFeedback(expectedFundingFeedback)
                .build();

        Assessment assessment = newAssessment().withId(6L).withFundingDecision(assessmentFundingDecisionOutcome).build();

        when(assessmentRepositoryMock.findById(assessmentId)).thenReturn(Optional.of(assessment));
        when(assessorFormInputResponseRepositoryMock.findByAssessmentId(assessmentId)).thenReturn(assessorFormInputResponses);

        ApplicationAssessmentResource applicationAssessmentResource = assessorFormInputResponseService.getApplicationAssessment(applicationId, assessmentId).getSuccess();

        assertEquals(new BigDecimal("95.0"), applicationAssessmentResource.getAveragePercentage());
        assertEquals(2, applicationAssessmentResource.getFeedback().size());
        assertEquals("Feedback 1", applicationAssessmentResource.getFeedback().get(1L));
        assertEquals("Feedback 2", applicationAssessmentResource.getFeedback().get(2L));
        assertEquals(new BigDecimal("5"), applicationAssessmentResource.getScores().get(1L));
        assertEquals(new BigDecimal("9"), applicationAssessmentResource.getScores().get(2L));
        assertEquals(true, applicationAssessmentResource.isInScope());
    }

    @Test
    public void getApplicationAssessments() {
        long applicationId = 1L;

        List<FormInput> scoreFormInputs = newFormInput()
                .withType(ASSESSOR_SCORE)
                .withQuestion(newQuestion().withId(1L, 2L).withAssessorMaximumScore(5, 10).buildArray(2, Question.class))
                .build(2);

        List<FormInput> feedbackFormInputs = newFormInput()
                .withType(TEXTAREA)
                .withQuestion(newQuestion().withId(1L, 2L).buildArray(2, Question.class))
                .build(2);

        FormInput scopeInput = newFormInput()
                .withType(ASSESSOR_APPLICATION_IN_SCOPE)
                .build();

        List<AssessorFormInputResponse> assessorForm1InputResponses = newAssessorFormInputResponse()
                .withFormInput(scoreFormInputs.get(0), scoreFormInputs.get(1), feedbackFormInputs.get(0), feedbackFormInputs.get(1), scopeInput)
                .withValue("5", "9", "Feedback 1","Feedback 2", "true")
                .build(5);

        List<AssessorFormInputResponse> assessorForm2InputResponses = newAssessorFormInputResponse()
                .withFormInput(scoreFormInputs.get(0), scoreFormInputs.get(1), feedbackFormInputs.get(0), feedbackFormInputs.get(1), scopeInput)
                .withValue("4", "8", "Feedback 3","Feedback 4", "false")
                .build(5);

        Long[] expectedIds = {1L, 2L};
        Boolean[] expectedFundingConfirmations = {TRUE, TRUE};
        String[] expectedFundingFeedbacks = {"Feedback 1", "Feedback 2"};

        List<AssessmentFundingDecisionOutcome> assessmentFundingDecisionOutcomes = newAssessmentFundingDecisionOutcome()
                .withId(expectedIds)
                .withFundingConfirmation(expectedFundingConfirmations)
                .withFeedback(expectedFundingFeedbacks)
                .build(2);

        List<Assessment> assessmentList = new ArrayList<Assessment>();
        assessmentList.add(newAssessment().withId(6L).withFundingDecision(assessmentFundingDecisionOutcomes.get(0)).build());
        assessmentList.add(newAssessment().withId(8L).withFundingDecision(assessmentFundingDecisionOutcomes.get(1)).build());

        when(assessmentRepositoryMock.findById(6L)).thenReturn(Optional.of(assessmentList.get(0)));
        when(assessmentRepositoryMock.findById(8L)).thenReturn(Optional.of(assessmentList.get(1)));
        when(assessmentRepositoryMock.findByTargetIdAndActivityStateIn(applicationId, Collections.singleton(AssessmentState.SUBMITTED))).thenReturn(assessmentList);

        when(assessorFormInputResponseRepositoryMock.findByAssessmentId(6L)).thenReturn(assessorForm1InputResponses);
        when(assessorFormInputResponseRepositoryMock.findByAssessmentId(8L)).thenReturn(assessorForm2InputResponses);

        ApplicationAssessmentsResource applicationAssessmentsResource = assessorFormInputResponseService.getApplicationAssessments(applicationId).getSuccess();

        ApplicationAssessmentResource form1 = applicationAssessmentsResource.getAssessments().get(0);
        ApplicationAssessmentResource form2 = applicationAssessmentsResource.getAssessments().get(1);

        assertEquals(new BigDecimal("95.0"), form1.getAveragePercentage());
        assertEquals(2, form1.getFeedback().size());
        assertEquals("Feedback 1", form1.getFeedback().get(1L));
        assertEquals("Feedback 2", form1.getFeedback().get(2L));
        assertEquals(new BigDecimal("5"), form1.getScores().get(1L));
        assertEquals(new BigDecimal("9"), form1.getScores().get(2L));
        assertEquals(true, form1.isInScope());

        assertEquals(new BigDecimal("80.0"), form2.getAveragePercentage());
        assertEquals(2, form2.getFeedback().size());
        assertEquals("Feedback 3", form2.getFeedback().get(1L));
        assertEquals("Feedback 4", form2.getFeedback().get(2L));
        assertEquals(new BigDecimal("4"), form2.getScores().get(1L));
        assertEquals(new BigDecimal("8"), form2.getScores().get(2L));
        assertEquals(false, form2.isInScope());
    }
}
