package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.assessment.builder.ApplicationAssessmentFeedbackResourceBuilder.newApplicationAssessmentFeedbackResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeResourceBuilder.newAssessmentFundingDecisionOutcomeResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentRejectOutcomeResourceBuilder.newAssessmentRejectOutcomeResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentSubmissionsResourceBuilder.newAssessmentSubmissionsResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentTotalScoreResourceBuilder.newAssessmentTotalScoreResource;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.assessmentResourceListType;
import static org.junit.Assert.*;
import static org.springframework.http.HttpStatus.OK;

public class AssessmentRestServiceImplTest extends BaseRestServiceUnitTest<AssessmentRestServiceImpl> {

    private static final String assessmentRestURL = "/assessment";

    @Override
    protected AssessmentRestServiceImpl registerRestServiceUnderTest() {
        final AssessmentRestServiceImpl assessmentRestService = new AssessmentRestServiceImpl();
        assessmentRestService.setAssessmentRestURL(assessmentRestURL);
        return assessmentRestService;
    }

    @Test
    public void getById() {
        AssessmentResource expected = newAssessmentResource().build();

        Long assessmentId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s", assessmentRestURL, assessmentId), AssessmentResource.class, expected);
        assertSame(expected, service.getById(assessmentId).getSuccess());
    }

    @Test
    public void getAssignableById() {
        AssessmentResource expected = newAssessmentResource().build();

        Long assessmentId = 1L;
        setupGetWithRestResultExpectations(format("%s/%s/assign", assessmentRestURL, assessmentId), AssessmentResource.class, expected);
        assertSame(expected, service.getAssignableById(assessmentId).getSuccess());
    }

    @Test
    public void getRejectableById() {
        AssessmentResource expected = newAssessmentResource().build();

        Long assessmentId = 1L;
        setupGetWithRestResultExpectations(format("%s/%s/rejectable", assessmentRestURL, assessmentId),
                AssessmentResource.class, expected);
        assertSame(expected, service.getRejectableById(assessmentId).getSuccess());
    }

    @Test
    public void getByUserAndCompetition() {
        List<AssessmentResource> expected = newAssessmentResource().build(2);

        Long userId = 1L;
        Long competitionId = 2L;

        setupGetWithRestResultExpectations(format("%s/user/%s/competition/%s", assessmentRestURL, userId, competitionId), assessmentResourceListType(), expected);
        assertSame(expected, service.getByUserAndCompetition(userId, competitionId).getSuccess());
    }

    @Test
    public void getByUserAndApplication() {
        List<AssessmentResource> expected = newAssessmentResource().build(2);

        Long userId = 1L;
        Long applicationId = 2L;

        setupGetWithRestResultExpectations(format("%s/user/%s/application/%s", assessmentRestURL, userId, applicationId), assessmentResourceListType(), expected);
        assertSame(expected, service.getByUserAndApplication(userId, applicationId).getSuccess());
    }

    @Test
    public void countByStateAndCompetition() {
        Long expected = 2L;

        AssessmentState state = AssessmentState.CREATED;
        Long competitionId = 2L;

        setupGetWithRestResultExpectations(format("%s/state/%s/competition/%s/count", assessmentRestURL, state, competitionId), Long.TYPE, expected);
        assertSame(expected, service.countByStateAndCompetition(state, competitionId).getSuccess());
    }

    @Test
    public void getTotalScore() {
        AssessmentTotalScoreResource expected = newAssessmentTotalScoreResource().build();

        Long assessmentId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/score", assessmentRestURL, assessmentId), AssessmentTotalScoreResource.class, expected);
        assertSame(expected, service.getTotalScore(assessmentId).getSuccess());
    }

    @Test
    public void recommend() {
        Long assessmentId = 1L;

        AssessmentFundingDecisionOutcomeResource assessmentFundingDecisionOutcomeResource =
                newAssessmentFundingDecisionOutcomeResource().build();
        setupPutWithRestResultExpectations(format("%s/%s/recommend", assessmentRestURL, assessmentId), assessmentFundingDecisionOutcomeResource, OK);
        RestResult<Void> response = service.recommend(assessmentId, assessmentFundingDecisionOutcomeResource);
        assertTrue(response.isSuccess());
    }

    @Test
    public void getApplicationFeedback() {
        long applicationId = 1L;

        ApplicationAssessmentFeedbackResource expectedResource = newApplicationAssessmentFeedbackResource().build();

        setupGetWithRestResultExpectations(
                format("%s/application/%s/feedback", assessmentRestURL, applicationId),
                ApplicationAssessmentFeedbackResource.class,
                expectedResource,
                OK
        );

        RestResult<ApplicationAssessmentFeedbackResource> response = service.getApplicationFeedback(applicationId);
        assertTrue(response.isSuccess());
    }

    @Test
    public void rejectInvitation() {
        Long assessmentId = 1L;

        AssessmentRejectOutcomeResource assessmentRejectOutcomeResource = newAssessmentRejectOutcomeResource().build();
        setupPutWithRestResultExpectations(format("%s/%s/reject-invitation", assessmentRestURL, assessmentId),
                assessmentRejectOutcomeResource, OK);
        RestResult<Void> response = service.rejectInvitation(assessmentId, assessmentRejectOutcomeResource);
        assertTrue(response.isSuccess());
    }

    @Test
    public void accept() {
        Long assessmentId = 1L;

        setupPutWithRestResultExpectations(format("%s/%s/accept-invitation", assessmentRestURL, assessmentId), null, OK);
        RestResult<Void> response = service.acceptInvitation(assessmentId);
        assertTrue(response.isSuccess());
    }

    @Test
    public void submitAssessments() {
        AssessmentSubmissionsResource assessmentSubmissions = newAssessmentSubmissionsResource().build();

        setupPutWithRestResultExpectations(format("%s/submit-assessments", assessmentRestURL), assessmentSubmissions, OK);
        RestResult<Void> response = service.submitAssessments(assessmentSubmissions);
        assertTrue(response.isSuccess());
    }

    @Test
    public void withdrawAssessment() {
        Long assessmentId = 1L;

        setupPutWithRestResultExpectations(format("%s/%s/withdraw", assessmentRestURL, assessmentId), null, OK);
        RestResult<Void> response = service.withdrawAssessment(assessmentId);
        assertTrue(response.isSuccess());
    }
    
    @Test
    public void createAssessment() {
        AssessmentCreateResource resource = new AssessmentCreateResource(1L, 2L);
        AssessmentResource assessment = new AssessmentResource();

        setupPostWithRestResultExpectations(assessmentRestURL, AssessmentResource.class, resource, assessment, OK);
        RestResult<AssessmentResource> response = service.createAssessment(resource);
        assertTrue(response.isSuccess());
        assertEquals(assessment, response.getSuccess());
    }

    @Test
    public void createAssessments() {
        List<AssessmentCreateResource> resource = singletonList(new AssessmentCreateResource(1L, 2L));
        List<AssessmentResource> assessment = singletonList(new AssessmentResource());

        setupPostWithRestResultExpectations(format("%s/bulk", assessmentRestURL), new ParameterizedTypeReference<List<AssessmentResource>>() {}, resource, assessment, OK);
        RestResult<List<AssessmentResource>> response = service.createAssessments(resource);
        assertTrue(response.isSuccess());
        assertEquals(assessment, response.getSuccess());
    }
}
