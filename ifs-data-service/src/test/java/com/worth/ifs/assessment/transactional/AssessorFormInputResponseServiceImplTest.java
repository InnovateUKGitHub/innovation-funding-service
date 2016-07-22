package com.worth.ifs.assessment.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.assessment.domain.AssessorFormInputResponse;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.commons.service.ServiceResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;

import java.util.List;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.AssessorFormInputResponseBuilder.newAssessorFormInputResponse;
import static com.worth.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static com.worth.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class AssessorFormInputResponseServiceImplTest extends BaseUnitTestMocksTest {
    @InjectMocks
    private AssessorFormInputResponseService assessorFormInputResponseService = new AssessorFormInputResponseServiceImpl();

    @Before
    public void setUp() throws Exception {
        when(assessorFormInputResponseMapperMock.mapToDomain(any(AssessorFormInputResponseResource.class))).thenAnswer(invocation -> {
            final AssessorFormInputResponseResource assessorFormInputResponseResource = invocation.getArgumentAt(0, AssessorFormInputResponseResource.class);
            return newAssessorFormInputResponse()
                    .with(id(assessorFormInputResponseResource.getId()))
                    .withAssessment(newAssessment().with(id(assessorFormInputResponseResource.getAssessment())).build())
                    .withFormInput(newFormInput().with(id(assessorFormInputResponseResource.getFormInput())).build())
                    .withValue(assessorFormInputResponseResource.getValue())
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
        final Long assessmentId = 1L;
        final Long formInputId = 2L;
        final String value = "New feedback";
        final String oldValue = "Old feedback";
        final AssessorFormInputResponse existingAssessorFormInputResponse = newAssessorFormInputResponse().build();
        final AssessorFormInputResponseResource existingAssessorFormInputResponseResource = newAssessorFormInputResponseResource()
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(oldValue)
                .build();

        final ArgumentCaptor<AssessorFormInputResponse> argument = ArgumentCaptor.forClass(AssessorFormInputResponse.class);

        when(assessorFormInputResponseRepositoryMock.findByAssessmentIdAndFormInputId(assessmentId, formInputId)).thenReturn(existingAssessorFormInputResponse);
        when(assessorFormInputResponseMapperMock.mapToResource(same(existingAssessorFormInputResponse))).thenReturn(existingAssessorFormInputResponseResource);

        final ServiceResult<Void> result = assessorFormInputResponseService.updateFormInputResponse(assessmentId, formInputId, value);
        assertTrue(result.isSuccess());
        verify(assessorFormInputResponseRepositoryMock, times(1)).findByAssessmentIdAndFormInputId(assessmentId, formInputId);
        verify(assessorFormInputResponseRepositoryMock, times(1)).save(argument.capture());

        final AssessorFormInputResponse saved = argument.getValue();
        assertEquals(existingAssessorFormInputResponseResource.getId(), saved.getId());
        assertEquals(existingAssessorFormInputResponseResource.getAssessment(), saved.getAssessment().getId());
        assertEquals(existingAssessorFormInputResponseResource.getFormInput(), saved.getFormInput().getId());
        assertEquals(value, saved.getValue());
    }

}