package com.worth.ifs.assessment.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.assessment.domain.AssessorFormInputResponse;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.form.resource.FormInputResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.AssessorFormInputResponseBuilder.newAssessorFormInputResponse;
import static com.worth.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static com.worth.ifs.commons.error.Error.fieldError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.form.builder.FormInputBuilder.newFormInput;
import static com.worth.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static java.time.LocalDateTime.now;
import static java.util.Collections.nCopies;
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
    public void testGetAllAssessorFormInputResponses() throws Exception {
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
    public void testGetAllAssessorFormInputResponsesByAssessmentAndQuestion() throws Exception {
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
    public void testUpdateFormInputResponse() throws Exception {
        Long assessmentId = 1L;
        Long formInputId = 2L;
        String value = "New feedback";
        String oldValue = "Old feedback";
        LocalDateTime oldUpdatedDate = now().minusHours(1);
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
        ArgumentCaptor<AssessorFormInputResponse> argument = ArgumentCaptor.forClass(AssessorFormInputResponse.class);

        when(formInputServiceMock.findFormInput(formInputId)).thenReturn(serviceSuccess(formInput));
        when(assessorFormInputResponseRepositoryMock.findByAssessmentIdAndFormInputId(assessmentId, formInputId)).thenReturn(existingAssessorFormInputResponse);
        when(assessorFormInputResponseMapperMock.mapToResource(same(existingAssessorFormInputResponse))).thenReturn(existingAssessorFormInputResponseResource);

        ServiceResult<Void> result = assessorFormInputResponseService.updateFormInputResponse(updatedAssessorFormInputResponseResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = Mockito.inOrder(assessorFormInputResponseRepositoryMock, assessorFormInputResponseMapperMock);
        inOrder.verify(assessorFormInputResponseRepositoryMock, calls(1)).findByAssessmentIdAndFormInputId(assessmentId, formInputId);
        inOrder.verify(assessorFormInputResponseMapperMock, calls(1)).mapToResource(same(existingAssessorFormInputResponse));
        inOrder.verify(assessorFormInputResponseMapperMock, calls(1)).mapToDomain(same(existingAssessorFormInputResponseResource));
        inOrder.verify(assessorFormInputResponseRepositoryMock, calls(1)).save(argument.capture());
        inOrder.verifyNoMoreInteractions();

        AssessorFormInputResponse saved = argument.getValue();
        assertEquals(existingAssessorFormInputResponseResource.getId(), saved.getId());
        assertEquals(existingAssessorFormInputResponseResource.getAssessment(), saved.getAssessment().getId());
        assertEquals(existingAssessorFormInputResponseResource.getFormInput(), saved.getFormInput().getId());
        assertEquals(value, saved.getValue());
        assertTrue(saved.getUpdatedDate().isAfter(oldUpdatedDate));
    }

    @Test
    public void testUpdateFormInputResponse_notExists() throws Exception {
        Long assessmentId = 1L;
        Long formInputId = 2L;
        String value = "New feedback";

        ArgumentCaptor<AssessorFormInputResponse> argument = ArgumentCaptor.forClass(AssessorFormInputResponse.class);
        AssessorFormInputResponseResource assessorFormInputResponseResource = newAssessorFormInputResponseResource()
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(value)
                .build();
        FormInputResource formInput = newFormInputResource()
                .withId(formInputId)
                .withWordCount(10)
                .build();
        when(formInputServiceMock.findFormInput(formInputId)).thenReturn(serviceSuccess(formInput));

        ServiceResult<Void> result = assessorFormInputResponseService.updateFormInputResponse(assessorFormInputResponseResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = Mockito.inOrder(assessorFormInputResponseRepositoryMock, assessorFormInputResponseMapperMock);
        inOrder.verify(assessorFormInputResponseRepositoryMock, calls(1)).findByAssessmentIdAndFormInputId(assessmentId, formInputId);
        inOrder.verify(assessorFormInputResponseMapperMock, calls(1)).mapToDomain(isA(AssessorFormInputResponseResource.class));
        inOrder.verify(assessorFormInputResponseRepositoryMock, calls(1)).save(argument.capture());
        inOrder.verifyNoMoreInteractions();

        AssessorFormInputResponse saved = argument.getValue();
        assertNull(saved.getId());
        assertEquals(assessmentId, saved.getAssessment().getId());
        assertEquals(formInputId, saved.getFormInput().getId());
        assertEquals(value, saved.getValue());
        assertNotNull(saved.getUpdatedDate());
    }

    @Test
    public void testUpdateFormInputResponse_nullValue() throws Exception {
        Long assessmentId = 1L;
        Long formInputId = 2L;
        String value = null;

        ArgumentCaptor<AssessorFormInputResponse> argument = ArgumentCaptor.forClass(AssessorFormInputResponse.class);
        AssessorFormInputResponseResource assessorFormInputResponseResource = newAssessorFormInputResponseResource()
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(value)
                .build();
        FormInputResource formInput = newFormInputResource()
                .withId(formInputId)
                .withWordCount(10)
                .build();
        when(formInputServiceMock.findFormInput(formInputId)).thenReturn(serviceSuccess(formInput));

        ServiceResult<Void> result = assessorFormInputResponseService.updateFormInputResponse(assessorFormInputResponseResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = Mockito.inOrder(assessorFormInputResponseRepositoryMock, assessorFormInputResponseMapperMock);
        inOrder.verify(assessorFormInputResponseRepositoryMock, calls(1)).findByAssessmentIdAndFormInputId(assessmentId, formInputId);
        inOrder.verify(assessorFormInputResponseMapperMock, calls(1)).mapToDomain(isA(AssessorFormInputResponseResource.class));
        inOrder.verify(assessorFormInputResponseRepositoryMock, calls(1)).save(argument.capture());
        inOrder.verifyNoMoreInteractions();

        AssessorFormInputResponse saved = argument.getValue();
        assertNull(saved.getId());
        assertEquals(assessmentId, saved.getAssessment().getId());
        assertEquals(formInputId, saved.getFormInput().getId());
        assertNull(saved.getValue());
        assertNotNull(saved.getUpdatedDate());
    }

    @Test
    public void testUpdateFormInputResponse_emptyValue() throws Exception {
        Long assessmentId = 1L;
        Long formInputId = 2L;
        String value = "";

        ArgumentCaptor<AssessorFormInputResponse> argument = ArgumentCaptor.forClass(AssessorFormInputResponse.class);
        AssessorFormInputResponseResource assessorFormInputResponseResource = newAssessorFormInputResponseResource()
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(value)
                .build();
        FormInputResource formInput = newFormInputResource()
                .withId(formInputId)
                .withWordCount(10)
                .build();
        when(formInputServiceMock.findFormInput(formInputId)).thenReturn(serviceSuccess(formInput));

        ServiceResult<Void> result = assessorFormInputResponseService.updateFormInputResponse(assessorFormInputResponseResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = Mockito.inOrder(assessorFormInputResponseRepositoryMock, assessorFormInputResponseMapperMock);
        inOrder.verify(assessorFormInputResponseRepositoryMock, calls(1)).findByAssessmentIdAndFormInputId(assessmentId, formInputId);
        inOrder.verify(assessorFormInputResponseMapperMock, calls(1)).mapToDomain(isA(AssessorFormInputResponseResource.class));
        inOrder.verify(assessorFormInputResponseRepositoryMock, calls(1)).save(argument.capture());
        inOrder.verifyNoMoreInteractions();

        AssessorFormInputResponse saved = argument.getValue();
        assertNull(saved.getId());
        assertEquals(assessmentId, saved.getAssessment().getId());
        assertEquals(formInputId, saved.getFormInput().getId());
        // Make sure that the empty string value is reduced to null
        assertNull(saved.getValue());
        assertNotNull(saved.getUpdatedDate());
    }

    @Test
    public void testUpdateFormInputResponse_exceedsWordLimit() throws Exception {
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

        ServiceResult<Void> result = assessorFormInputResponseService.updateFormInputResponse(assessorFormInputResponseResource);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(fieldError("value", value, "validation.field.max.word.count", "", 100)));
    }

    @Test
    public void testUpdateFormInputResponse_noWordLimit() throws Exception {
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

        ServiceResult<Void> result = assessorFormInputResponseService.updateFormInputResponse(assessorFormInputResponseResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testUpdateFormInputResponse_sameAsExistingValue() throws Exception {
        Long assessmentId = 1L;
        Long formInputId = 2L;
        String value = "Value that won't be touched";
        String oldValue = "Value that won't be touched";
        LocalDateTime oldUpdatedDate = now().minusHours(1);
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
        when(formInputServiceMock.findFormInput(formInputId)).thenReturn(serviceSuccess(formInput));

        ArgumentCaptor<AssessorFormInputResponse> argument = ArgumentCaptor.forClass(AssessorFormInputResponse.class);

        when(assessorFormInputResponseRepositoryMock.findByAssessmentIdAndFormInputId(assessmentId, formInputId)).thenReturn(existingAssessorFormInputResponse);
        when(assessorFormInputResponseMapperMock.mapToResource(same(existingAssessorFormInputResponse))).thenReturn(existingAssessorFormInputResponseResource);

        ServiceResult<Void> result = assessorFormInputResponseService.updateFormInputResponse(assessorFormInputResponseResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = Mockito.inOrder(assessorFormInputResponseRepositoryMock, assessorFormInputResponseMapperMock);
        inOrder.verify(assessorFormInputResponseRepositoryMock, calls(1)).findByAssessmentIdAndFormInputId(assessmentId, formInputId);
        inOrder.verify(assessorFormInputResponseMapperMock, calls(1)).mapToResource(same(existingAssessorFormInputResponse));
        inOrder.verify(assessorFormInputResponseMapperMock, calls(1)).mapToDomain(same(existingAssessorFormInputResponseResource));
        inOrder.verify(assessorFormInputResponseRepositoryMock, calls(1)).save(argument.capture());
        inOrder.verifyNoMoreInteractions();

        AssessorFormInputResponse saved = argument.getValue();
        assertEquals(existingAssessorFormInputResponseResource.getId(), saved.getId());
        assertEquals(existingAssessorFormInputResponseResource.getAssessment(), saved.getAssessment().getId());
        assertEquals(existingAssessorFormInputResponseResource.getFormInput(), saved.getFormInput().getId());
        assertEquals(value, saved.getValue());

        // The updated date should not have been touched since the value was the same
        assertEquals(oldUpdatedDate, saved.getUpdatedDate());
    }

}