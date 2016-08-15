package com.worth.ifs.assessment.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.assessment.builder.ProcessOutcomeResourceBuilder.newProcessOutcomeResource;
import static java.lang.String.format;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class AssessmentRestServiceImplTest extends BaseRestServiceUnitTest<AssessmentRestServiceImpl> {

    private static final String assessmentRestURL = "/assessment";

    @Before
    public void setUp() throws Exception {

    }

    @Override
    protected AssessmentRestServiceImpl registerRestServiceUnderTest() {
        final AssessmentRestServiceImpl assessmentRestService = new AssessmentRestServiceImpl();
        assessmentRestService.setAssessmentRestURL(assessmentRestURL);
        return assessmentRestService;
    }

    @Test
    public void getById() throws Exception {
        final AssessmentResource expected = newAssessmentResource()
                .build();

        final Long assessmentId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s", assessmentRestURL, assessmentId), AssessmentResource.class, expected, OK);
        final AssessmentResource response = service.getById(assessmentId).getSuccessObject();
        assertSame(expected, response);
    }

    @Test
    public void recommend() throws Exception {
        final Long assessmentId = 1L;

        ProcessOutcomeResource processOutcome = newProcessOutcomeResource().build();
        setupPutWithRestResultExpectations(format("%s/%s/recommend", assessmentRestURL, assessmentId), processOutcome, OK);
        final RestResult<Void> response = service.recommend(assessmentId, processOutcome);
        assertTrue(response.isSuccess());
    }

    @Test
    public void rejectInvitation() throws Exception {
        final Long assessmentId = 1L;

        ProcessOutcomeResource processOutcome = newProcessOutcomeResource().build();
        setupPutWithRestResultExpectations(format("%s/%s/rejectInvitation", assessmentRestURL, assessmentId), processOutcome, OK);
        final RestResult<Void> response = service.rejectInvitation(assessmentId, processOutcome);
        assertTrue(response.isSuccess());
    }
}