package com.worth.ifs.assessment.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.commons.rest.RestResult;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.assessorFormInputResponseResourceListType;
import static java.lang.String.format;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class AssessorFormInputResponseRestServiceImplTest extends BaseRestServiceUnitTest<AssessorFormInputResponseRestServiceImpl> {
    private static String assessorFormInputResponseRestUrl = "/assessment";

    @Override
    protected AssessorFormInputResponseRestServiceImpl registerRestServiceUnderTest() {
        AssessorFormInputResponseRestServiceImpl assessorFormInputResponseRestService = new AssessorFormInputResponseRestServiceImpl();
        assessorFormInputResponseRestService.setAssessorFormInputResponseRestUrl(assessorFormInputResponseRestUrl);
        return assessorFormInputResponseRestService;
    }

    @Test
    public void testGetAllAssessorFormInputResponses() throws Exception {
        List<AssessorFormInputResponseResource> expected = newAssessorFormInputResponseResource()
                .build(2);

        Long assessmentId = 1L;

        setupGetWithRestResultExpectations(format("%s/assessment/%s", assessorFormInputResponseRestUrl, assessmentId), assessorFormInputResponseResourceListType(), expected, OK);
        List<AssessorFormInputResponseResource> response = service.getAllAssessorFormInputResponses(assessmentId).getSuccessObject();
        assertSame(expected, response);
    }

    @Test
    public void testGetAllAssessorFormInputResponsesByAssessmentAndQuestion() throws Exception {
        List<AssessorFormInputResponseResource> expected = newAssessorFormInputResponseResource()
                .build(2);

        Long assessmentId = 1L;
        Long questionId = 2L;

        setupGetWithRestResultExpectations(format("%s/assessment/%s/question/%s", assessorFormInputResponseRestUrl, assessmentId, questionId), assessorFormInputResponseResourceListType(), expected, OK);
        List<AssessorFormInputResponseResource> response = service.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId).getSuccessObject();
        assertSame(expected, response);
    }

    @Test
    public void testUpdateFormInputResponse() throws Exception {
        Long assessmentId = 1L;
        Long formInputId = 2L;
        String value = "Feedback";

        AssessorFormInputResponseResource formInputResponse = newAssessorFormInputResponseResource()
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(value)
                .build();
        setupPutWithRestResultExpectations(format("%s", assessorFormInputResponseRestUrl), formInputResponse, OK);
        final RestResult<Void> response = service.updateFormInputResponse(formInputResponse);
        assertTrue(response.isSuccess());
    }
}