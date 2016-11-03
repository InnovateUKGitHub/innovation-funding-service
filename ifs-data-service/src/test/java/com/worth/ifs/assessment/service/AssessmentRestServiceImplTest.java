package com.worth.ifs.assessment.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.assessment.resource.ApplicationRejectionResource;
import com.worth.ifs.assessment.resource.AssessmentFundingDecisionResource;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.commons.rest.RestResult;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.assessment.builder.ApplicationRejectionResourceBuilder.newApplicationRejectionResource;
import static com.worth.ifs.assessment.builder.AssessmentFundingDecisionResourceBuilder.newAssessmentFundingDecisionResource;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.assessmentResourceListType;
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
        AssessmentResource expected = newAssessmentResource().build();

        Long assessmentId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s", assessmentRestURL, assessmentId), AssessmentResource.class, expected, OK);
        AssessmentResource response = service.getById(assessmentId).getSuccessObject();
        assertSame(expected, response);
    }

    @Test
    public void getByUserAndCompetition() throws Exception {
        List<AssessmentResource> expected = newAssessmentResource().build(2);

        Long userId = 1L;
        Long competitionId = 2L;

        setupGetWithRestResultExpectations(format("%s/user/%s/competition/%s", assessmentRestURL, userId, competitionId), assessmentResourceListType(), expected, OK);
        List<AssessmentResource> response = service.getByUserAndCompetition(userId, competitionId).getSuccessObject();
        assertSame(expected, response);
    }

    @Test
    public void recommend() throws Exception {
        Long assessmentId = 1L;

        AssessmentFundingDecisionResource assessmentFundingDecision = newAssessmentFundingDecisionResource().build();
        setupPutWithRestResultExpectations(format("%s/%s/recommend", assessmentRestURL, assessmentId), assessmentFundingDecision, OK);
        RestResult<Void> response = service.recommend(assessmentId, assessmentFundingDecision);
        assertTrue(response.isSuccess());
    }

    @Test
    public void rejectInvitation() throws Exception {
        Long assessmentId = 1L;

        ApplicationRejectionResource applicationRejection = newApplicationRejectionResource().build();
        setupPutWithRestResultExpectations(format("%s/%s/rejectInvitation", assessmentRestURL, assessmentId), applicationRejection, OK);
        RestResult<Void> response = service.rejectInvitation(assessmentId, applicationRejection);
        assertTrue(response.isSuccess());
    }

    @Test
    public void accept() throws Exception {
        Long assessmentId = 1L;

        setupPutWithRestResultExpectations(format("%s/%s/acceptInvitation", assessmentRestURL, assessmentId), null, OK);
        RestResult<Void> response = service.acceptInvitation(assessmentId);
        assertTrue(response.isSuccess());
    }
}