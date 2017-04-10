package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static java.time.ZonedDateTime.now;
import static java.util.Collections.nCopies;
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

        when(formInputServiceMock.findFormInput(formInputId)).thenReturn(serviceSuccess(formInput));
        when(assessorFormInputResponseRepositoryMock.findByAssessmentIdAndFormInputId(assessmentId, formInputId)).thenReturn(existingAssessorFormInputResponse);
        when(assessorFormInputResponseMapperMock.mapToResource(existingAssessorFormInputResponse)).thenReturn(existingAssessorFormInputResponseResource);

        ServiceResult<AssessorFormInputResponseResource> result = assessorFormInputResponseService.updateFormInputResponse(updatedAssessorFormInputResponseResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessorFormInputResponseRepositoryMock, assessorFormInputResponseMapperMock);
        inOrder.verify(assessorFormInputResponseRepositoryMock).findByAssessmentIdAndFormInputId(assessmentId, formInputId);
        inOrder.verify(assessorFormInputResponseMapperMock).mapToResource(same(existingAssessorFormInputResponse));
        inOrder.verifyNoMoreInteractions();

        AssessorFormInputResponseResource saved = result.toGetResponse().getSuccessObject();
        assertEquals(existingAssessorFormInputResponseResource.getId(), saved.getId());
        assertEquals(existingAssessorFormInputResponseResource.getAssessment(), saved.getAssessment());
        assertEquals(existingAssessorFormInputResponseResource.getFormInput(), saved.getFormInput());
        assertEquals(value, saved.getValue());
        assertTrue(saved.getUpdatedDate().isAfter(oldUpdatedDate));
    }

    @Test
    public void updateFormInputResponse_notExists() throws Exception {
        Long assessmentId = 1L;
        Long formInputId = 2L;
        String value = "New feedback";
        ZonedDateTime oldUpdatedDate = now().minusHours(1);
        AssessorFormInputResponseResource updatedAssessorFormInputResponseResource = newAssessorFormInputResponseResource()
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(value)
                .build();
        FormInputResource formInput = newFormInputResource()
                .withId(formInputId)
                .withWordCount(10)
                .build();

        when(formInputServiceMock.findFormInput(formInputId)).thenReturn(serviceSuccess(formInput));

        ServiceResult<AssessorFormInputResponseResource> result = assessorFormInputResponseService.updateFormInputResponse(updatedAssessorFormInputResponseResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessorFormInputResponseRepositoryMock, assessorFormInputResponseMapperMock);
        inOrder.verify(assessorFormInputResponseRepositoryMock).findByAssessmentIdAndFormInputId(assessmentId, formInputId);
        inOrder.verifyNoMoreInteractions();

        AssessorFormInputResponseResource updated = result.toGetResponse().getSuccessObject();
        assertEquals(updatedAssessorFormInputResponseResource.getAssessment(), updated.getAssessment());
        assertEquals(updatedAssessorFormInputResponseResource.getFormInput(), updated.getFormInput());
        assertEquals(value, updated.getValue());
        assertTrue(updated.getUpdatedDate().isAfter(oldUpdatedDate));
    }

    @Test
    public void updateFormInputResponse_nullValue() throws Exception {
        Long assessmentId = 1L;
        Long formInputId = 2L;
        String value = null;
        ZonedDateTime oldUpdatedDate = now().minusHours(1);
        AssessorFormInputResponseResource updatedAssessorFormInputResponseResource = newAssessorFormInputResponseResource()
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(value)
                .build();
        FormInputResource formInput = newFormInputResource()
                .withId(formInputId)
                .withWordCount(10)
                .build();

        when(formInputServiceMock.findFormInput(formInputId)).thenReturn(serviceSuccess(formInput));

        ServiceResult<AssessorFormInputResponseResource> result = assessorFormInputResponseService.updateFormInputResponse(updatedAssessorFormInputResponseResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessorFormInputResponseRepositoryMock, assessorFormInputResponseMapperMock);
        inOrder.verify(assessorFormInputResponseRepositoryMock).findByAssessmentIdAndFormInputId(assessmentId, formInputId);
        inOrder.verifyNoMoreInteractions();

        AssessorFormInputResponseResource updated = result.toGetResponse().getSuccessObject();
        assertEquals(updatedAssessorFormInputResponseResource.getAssessment(), updated.getAssessment());
        assertEquals(updatedAssessorFormInputResponseResource.getFormInput(), updated.getFormInput());
        assertEquals(value, updated.getValue());
        assertTrue(updated.getUpdatedDate().isAfter(oldUpdatedDate));
    }

    @Test
    public void updateFormInputResponse_emptyValue() throws Exception {
        Long assessmentId = 1L;
        Long formInputId = 2L;
        String value = "";
        ZonedDateTime oldUpdatedDate = now().minusHours(1);
        AssessorFormInputResponseResource updatedAssessorFormInputResponseResource = newAssessorFormInputResponseResource()
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(value)
                .build();
        FormInputResource formInput = newFormInputResource()
                .withId(formInputId)
                .withWordCount(10)
                .build();

        when(formInputServiceMock.findFormInput(formInputId)).thenReturn(serviceSuccess(formInput));

        ServiceResult<AssessorFormInputResponseResource> result = assessorFormInputResponseService.updateFormInputResponse(updatedAssessorFormInputResponseResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessorFormInputResponseRepositoryMock, assessorFormInputResponseMapperMock);
        inOrder.verify(assessorFormInputResponseRepositoryMock).findByAssessmentIdAndFormInputId(assessmentId, formInputId);
        inOrder.verifyNoMoreInteractions();

        AssessorFormInputResponseResource updated = result.toGetResponse().getSuccessObject();
        assertEquals(updatedAssessorFormInputResponseResource.getAssessment(), updated.getAssessment());
        assertEquals(updatedAssessorFormInputResponseResource.getFormInput(), updated.getFormInput());
        // Make sure that the empty string value is reduced to null
        assertNull(updated.getValue());
        assertNotNull(updated.getUpdatedDate());
    }

    @Test
    @Ignore("Fix when 8538 is merged and a baseline has been run")
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
        when(formInputServiceMock.findFormInput(formInputId)).thenReturn(serviceSuccess(formInput));

        ServiceResult<AssessorFormInputResponseResource> result = assessorFormInputResponseService.updateFormInputResponse(assessorFormInputResponseResource);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(fieldError("value", value, "validation.field.max.word.count", "", 100)));
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
        when(formInputServiceMock.findFormInput(formInputId)).thenReturn(serviceSuccess(formInput));

        ServiceResult<AssessorFormInputResponseResource> result = assessorFormInputResponseService.updateFormInputResponse(assessorFormInputResponseResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void updateFormInputResponse_sameAsExistingValue() throws Exception {
        Long assessmentId = 1L;
        Long formInputId = 2L;
        String value = "Value shouldn't have been touched.";
        String oldValue = "Value shouldn't have been touched.";
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
                .withWordCount(10)
                .build();

        when(formInputServiceMock.findFormInput(formInputId)).thenReturn(serviceSuccess(formInput));
        when(assessorFormInputResponseRepositoryMock.findByAssessmentIdAndFormInputId(assessmentId, formInputId)).thenReturn(existingAssessorFormInputResponse);
        when(assessorFormInputResponseMapperMock.mapToResource(existingAssessorFormInputResponse)).thenReturn(existingAssessorFormInputResponseResource);

        ServiceResult<AssessorFormInputResponseResource> result = assessorFormInputResponseService.updateFormInputResponse(updatedAssessorFormInputResponseResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessorFormInputResponseRepositoryMock, assessorFormInputResponseMapperMock);
        inOrder.verify(assessorFormInputResponseRepositoryMock).findByAssessmentIdAndFormInputId(assessmentId, formInputId);
        inOrder.verify(assessorFormInputResponseMapperMock).mapToResource(same(existingAssessorFormInputResponse));
        inOrder.verifyNoMoreInteractions();

        AssessorFormInputResponseResource saved = result.toGetResponse().getSuccessObject();
        assertEquals(existingAssessorFormInputResponseResource.getId(), saved.getId());
        assertEquals(existingAssessorFormInputResponseResource.getAssessment(), saved.getAssessment());
        assertEquals(existingAssessorFormInputResponseResource.getFormInput(), saved.getFormInput());
        assertEquals(value, saved.getValue());

        // The updated date should not have been touched since the value was the same
        assertEquals(oldUpdatedDate, saved.getUpdatedDate());
    }

    @Test
    @Ignore("Fix when 8538 is merged and a baseline has been run")
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

        when(formInputServiceMock.findFormInput(formInputId)).thenReturn(serviceSuccess(formInput));
        when(categoryServiceMock.getResearchCategories()).thenReturn(serviceSuccess(Collections.singletonList(categoryResource)));
        when(assessorFormInputResponseRepositoryMock.findByAssessmentIdAndFormInputId(assessmentId, formInputId)).thenReturn(existingAssessorFormInputResponse);
        when(assessorFormInputResponseMapperMock.mapToResource(same(existingAssessorFormInputResponse))).thenReturn(existingAssessorFormInputResponseResource);

        ServiceResult<AssessorFormInputResponseResource> result = assessorFormInputResponseService.updateFormInputResponse(updatedAssessorFormInputResponseResource);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(fieldError("value", value, "org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException", "CategoryResource", value)));
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
        assertEquals(2,scores.getScores().keySet().size());
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
                .withValue("2","Feedback 1", "4", "Feedback 2")
                .build(4);

        when(assessorFormInputResponseRepositoryMock.findByAssessmentTargetIdAndFormInputQuestionId(applicationId, questionId)).thenReturn(assessorFormInputResponses);

        AssessmentFeedbackAggregateResource feedback = assessorFormInputResponseService.getAssessmentAggregateFeedback(applicationId, questionId).getSuccessObjectOrThrowException();

        assertEquals(new BigDecimal("3"), feedback.getAvgScore());
        assertEquals(2, feedback.getFeedback().size());
        assertEquals("Feedback 1", feedback.getFeedback().get(0));
        assertEquals("Feedback 2", feedback.getFeedback().get(1));
    }
}
