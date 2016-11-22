package com.worth.ifs.assessment.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.commons.service.ServiceResult;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.*;
import static com.worth.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class AssessorFormInputResponseServiceImplTest extends BaseServiceUnitTest<AssessorFormInputResponseService> {
    @Mock
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Override
    protected AssessorFormInputResponseService supplyServiceUnderTest() {
        return new AssessorFormInputResponseServiceImpl();
    }

    @Test
    public void testGetAllAssessorFormInputResponses() throws Exception {
        List<AssessorFormInputResponseResource> expected = newAssessorFormInputResponseResource()
                .build(2);

        Long assessmentId = 1L;

        when(assessorFormInputResponseRestService.getAllAssessorFormInputResponses(assessmentId)).thenReturn(restSuccess(expected));

        List<AssessorFormInputResponseResource> response = service.getAllAssessorFormInputResponses(assessmentId);

        assertSame(expected, response);
        verify(assessorFormInputResponseRestService, only()).getAllAssessorFormInputResponses(assessmentId);
    }


    @Test
    public void testGetAllAssessorFormInputResponsesByAssessmentAndQuestion() throws Exception {
        List<AssessorFormInputResponseResource> expected = newAssessorFormInputResponseResource()
                .build(2);

        Long assessmentId = 1L;
        Long questionId = 2L;

        when(assessorFormInputResponseRestService.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId)).thenReturn(restSuccess(expected));

        List<AssessorFormInputResponseResource> response = service.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId);

        assertSame(expected, response);
        verify(assessorFormInputResponseRestService, only()).getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId);
    }

    @Test
    public void updateFormInputResponse() throws Exception {
        Long assessmentId = 1L;
        Long formInputId = 2L;
        String value = "Feedback";

        AssessorFormInputResponseResource assessorFormInputResponse = newAssessorFormInputResponseResource()
                .with(id(null))
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(value)
                .build();
        when(assessorFormInputResponseRestService.updateFormInputResponse(assessorFormInputResponse)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.updateFormInputResponse(assessmentId, formInputId, value);
        assertTrue(result.isSuccess());

        verify(assessorFormInputResponseRestService, only()).updateFormInputResponse(assessorFormInputResponse);
    }

}