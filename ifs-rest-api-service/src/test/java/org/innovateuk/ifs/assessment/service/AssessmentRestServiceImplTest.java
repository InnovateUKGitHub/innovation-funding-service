package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.ApplicationRejectionResourceBuilder.newApplicationRejectionResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionResourceBuilder.newAssessmentFundingDecisionResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentSubmissionsResourceBuilder.newAssessmentSubmissionsResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentTotalScoreResourceBuilder.newAssessmentTotalScoreResource;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.assessmentResourceListType;
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

        setupGetWithRestResultExpectations(format("%s/%s", assessmentRestURL, assessmentId), AssessmentResource.class, expected);
        assertSame(expected, service.getById(assessmentId).getSuccessObject());
    }

    @Test
    public void getAssignableById() throws Exception {
        AssessmentResource expected = newAssessmentResource().build();

        Long assessmentId = 1L;
        setupGetWithRestResultExpectations(format("%s/%s/assign", assessmentRestURL, assessmentId), AssessmentResource.class, expected);
        assertSame(expected, service.getAssignableById(assessmentId).getSuccessObject());
    }

    @Test
    public void getByUserAndCompetition() throws Exception {
        List<AssessmentResource> expected = newAssessmentResource().build(2);

        Long userId = 1L;
        Long competitionId = 2L;

        setupGetWithRestResultExpectations(format("%s/user/%s/competition/%s", assessmentRestURL, userId, competitionId), assessmentResourceListType(), expected);
        assertSame(expected, service.getByUserAndCompetition(userId, competitionId).getSuccessObject());
    }

    @Test
    public void getTotalScore() throws Exception {
        AssessmentTotalScoreResource expected = newAssessmentTotalScoreResource().build();

        Long assessmentId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/score", assessmentRestURL, assessmentId), AssessmentTotalScoreResource.class, expected);
        assertSame(expected, service.getTotalScore(assessmentId).getSuccessObject());
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

    @Test
    public void submitAssessments() throws Exception {
        AssessmentSubmissionsResource assessmentSubmissions = newAssessmentSubmissionsResource().build();

        setupPutWithRestResultExpectations(format("%s/submitAssessments", assessmentRestURL), assessmentSubmissions, OK);
        RestResult<Void> response = service.submitAssessments(assessmentSubmissions);
        assertTrue(response.isSuccess());
    }
}
