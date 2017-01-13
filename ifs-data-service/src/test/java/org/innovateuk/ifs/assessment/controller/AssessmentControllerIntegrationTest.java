package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.ApplicationRejectionResourceBuilder.newApplicationRejectionResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionResourceBuilder.newAssessmentFundingDecisionResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.ACCEPTED;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.OPEN;
import static org.innovateuk.ifs.commons.error.CommonErrors.forbiddenError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AssessmentControllerIntegrationTest extends BaseControllerIntegrationTest<AssessmentController> {

    @Before
    public void setUp() throws Exception {
    }

    @Autowired
    @Override
    protected void setControllerUnderTest(final AssessmentController controller) {
        this.controller = controller;
    }

    @Test
    public void findById() throws Exception {
        Long assessmentId = 6L;

        loginFelixWilson();
        AssessmentResource assessmentResource = controller.findById(assessmentId).getSuccessObject();
        assertEquals(assessmentId, assessmentResource.getId());
        assertEquals(Long.valueOf(21L), assessmentResource.getProcessRole());
        assertEquals(Long.valueOf(4L), assessmentResource.getApplication());
        assertEquals(Long.valueOf(1L), assessmentResource.getCompetition());
        assertEquals(emptyList(), assessmentResource.getProcessOutcomes());
    }

    @Test
    public void findById_notFound() throws Exception {
        Long assessmentId = 999L;

        loginPaulPlum();
        RestResult<AssessmentResource> result = controller.findById(assessmentId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Assessment.class, 999L)));
    }

    @Test
    public void findById_notTheAssessmentOwner() throws Exception {
        Long assessmentId = 5L;

        loginSteveSmith();
        RestResult<AssessmentResource> result = controller.findById(assessmentId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(forbiddenError(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION)));
    }

    @Test
    public void findAssignableById() throws Exception {
        Long assessmentId = 4L;

        loginPaulPlum();
        AssessmentResource assessmentResource = controller.findAssignableById(assessmentId).getSuccessObject();
        assertEquals(Long.valueOf(17L), assessmentResource.getProcessRole());
        assertEquals(Long.valueOf(6L), assessmentResource.getApplication());
        assertEquals(Long.valueOf(1L), assessmentResource.getCompetition());
        assertEquals(emptyList(), assessmentResource.getProcessOutcomes());
    }

    @Test
    public void findAssignableById_notFound() throws Exception {
        Long assessmentId = 999L;

        loginPaulPlum();
        RestResult<AssessmentResource> result = controller.findAssignableById(assessmentId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Assessment.class, 999L)));
    }

    @Test
    public void findAssignableById_notTheAssessmentOwner() throws Exception {
        Long assessmentId = 5L;

        loginSteveSmith();
        RestResult<AssessmentResource> result = controller.findAssignableById(assessmentId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(forbiddenError(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION)));
    }

    @Test
    public void findAssignableById_notAssignable() throws Exception {
        Long assessmentId = 6L;

        loginFelixWilson();
        RestResult<AssessmentResource> result = controller.findAssignableById(assessmentId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(forbiddenError(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION)));
    }

    @Test
    public void findByUserAndCompetition() throws Exception {
        Long userId = 3L;
        Long competitionId = 1L;

        loginPaulPlum();
        RestResult<List<AssessmentResource>> result = controller.findByUserAndCompetition(userId, competitionId);
        assertTrue(result.isSuccess());
        List<AssessmentResource> assessmentResources = result.getSuccessObjectOrThrowException();
        assertEquals(4, assessmentResources.size());
    }

    @Test
    public void getTotalScore() throws Exception {
        loginPaulPlum();

        AssessmentTotalScoreResource result = controller.getTotalScore(1L).getSuccessObjectOrThrowException();
        assertEquals(72, result.getTotalScoreGiven());
        assertEquals(100, result.getTotalScorePossible());
    }

    @Test
    public void recommend() throws Exception {
        Long assessmentId = 2L;

        loginPaulPlum();
        AssessmentResource assessmentResource = controller.findById(assessmentId).getSuccessObject();
        assertEquals(OPEN, assessmentResource.getAssessmentState());

        AssessmentFundingDecisionResource assessmentFundingDecision = newAssessmentFundingDecisionResource().build();

        RestResult<Void> result = controller.recommend(assessmentResource.getId(), assessmentFundingDecision);
        assertTrue(result.isSuccess());

        AssessmentResource assessmentResult = controller.findById(assessmentId).getSuccessObject();
        assertEquals(OPEN, assessmentResult.getAssessmentState());
    }

    @Test
    public void rejectInvitation() {
        Long assessmentId = 2L;

        loginPaulPlum();
        AssessmentResource assessmentResource = controller.findById(assessmentId).getSuccessObject();
        assertEquals(OPEN, assessmentResource.getAssessmentState());

        ApplicationRejectionResource applicationRejection = newApplicationRejectionResource().build();

        RestResult<Void> result = controller.rejectInvitation(assessmentResource.getId(), applicationRejection);
        assertTrue(result.isSuccess());

        RestResult<AssessmentResource> assessmentResult = controller.findById(assessmentId);
        assertEquals(assessmentResult.getErrors().get(0).getErrorKey(), GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION.getErrorKey());
    }

    @Test
    public void rejectInvitation_eventNotAccepted() {
        Long assessmentId = 2L;

        loginPaulPlum();
        AssessmentResource assessmentResource = controller.findById(assessmentId).getSuccessObject();
        assertEquals(OPEN, assessmentResource.getAssessmentState());

        ApplicationRejectionResource applicationRejection = newApplicationRejectionResource().build();

        RestResult<Void> result = controller.rejectInvitation(assessmentResource.getId(), applicationRejection);
        assertTrue(result.isSuccess());

        RestResult<AssessmentResource> assessmentResult = controller.findById(assessmentId);
        assertEquals(assessmentResult.getErrors().get(0).getErrorKey(), GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION.getErrorKey());

        // Now reject the assessment again
        assertTrue(controller.rejectInvitation(assessmentId, applicationRejection).isFailure());
    }

    @Test
    public void accept() throws Exception {
        Long assessmentId = 4L;
        Long processRole = 17L;

        loginPaulPlum();
        AssessmentResource assessmentResource = controller.findById(assessmentId).getSuccessObject();
        assertEquals(AssessmentStates.PENDING, assessmentResource.getAssessmentState());
        assertEquals(processRole, assessmentResource.getProcessRole());

        RestResult<Void> result = controller.acceptInvitation(assessmentResource.getId());
        assertTrue(result.isSuccess());

        AssessmentResource assessmentResult = controller.findById(assessmentId).getSuccessObject();
        assertEquals(ACCEPTED, assessmentResult.getAssessmentState());
    }
}
