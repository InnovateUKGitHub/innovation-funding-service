package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.ApplicationRejectionResource;
import com.worth.ifs.assessment.resource.AssessmentFundingDecisionResource;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.commons.rest.RestResult;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.worth.ifs.assessment.builder.ApplicationRejectionResourceBuilder.newApplicationRejectionResource;
import static com.worth.ifs.assessment.builder.AssessmentFundingDecisionResourceBuilder.newAssessmentFundingDecisionResource;
import static com.worth.ifs.commons.error.CommonErrors.forbiddenError;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
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
    public void findByUserAndCompetition() throws Exception {
        Long userId = 3L;
        Long competitionId = 1L;

        loginPaulPlum();
        RestResult<List<AssessmentResource>> result = controller.findByUserAndCompetition(userId, competitionId);
        assertTrue(result.isSuccess());
        List<AssessmentResource> assessmentResources = result.getSuccessObjectOrThrowException();
        assertEquals(3, assessmentResources.size());
    }

    @Test
    public void recommend() throws Exception {
        Long assessmentId = 2L;
        Long processRole = 8L;

        loginPaulPlum();
        AssessmentResource assessmentResource = controller.findById(assessmentId).getSuccessObject();
        assertEquals(AssessmentStates.OPEN, assessmentResource.getAssessmentState());
        assertEquals(processRole, assessmentResource.getProcessRole());

        AssessmentFundingDecisionResource assessmentFundingDecision = newAssessmentFundingDecisionResource().build();

        RestResult<Void> result = controller.recommend(assessmentResource.getId(), assessmentFundingDecision);
        assertTrue(result.isSuccess());

        AssessmentResource assessmentResult = controller.findById(assessmentId).getSuccessObject();
        assertEquals(AssessmentStates.ASSESSED, assessmentResult.getAssessmentState());
    }

    @Ignore("TODO - should this be open -> open")
    @Test
    public void recommend_eventNotAccepted() throws Exception {
        Long assessmentId = 2L;
        Long processRole = 8L;

        loginPaulPlum();
        AssessmentResource assessmentResource = controller.findById(assessmentId).getSuccessObject();
        assertEquals(AssessmentStates.OPEN, assessmentResource.getAssessmentState());
        assertEquals(processRole, assessmentResource.getProcessRole());

        AssessmentFundingDecisionResource assessmentFundingDecision = newAssessmentFundingDecisionResource().build();

        RestResult<Void> result = controller.recommend(assessmentResource.getId(), assessmentFundingDecision);
        assertTrue(result.isSuccess());

        AssessmentResource assessmentResult = controller.findById(assessmentId).getSuccessObject();
        assertEquals(AssessmentStates.ASSESSED, assessmentResult.getAssessmentState());

        // Now recommend the assessment again
        assertTrue(controller.recommend(assessmentResource.getId(), assessmentFundingDecision).isFailure());
    }

    @Test
    public void rejectInvitation() {
        Long assessmentId = 2L;
        Long processRole = 8L;

        loginPaulPlum();
        AssessmentResource assessmentResource = controller.findById(assessmentId).getSuccessObject();
        assertEquals(AssessmentStates.OPEN, assessmentResource.getAssessmentState());
        assertEquals(processRole, assessmentResource.getProcessRole());

        ApplicationRejectionResource applicationRejection = newApplicationRejectionResource().build();

        RestResult<Void> result = controller.rejectInvitation(assessmentResource.getId(), applicationRejection);
        assertTrue(result.isSuccess());

        RestResult<AssessmentResource> assessmentResult = controller.findById(assessmentId);
        assertEquals(assessmentResult.getErrors().get(0).getErrorKey(), GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION.getErrorKey());
    }

    @Test
    public void rejectInvitation_eventNotAccepted() {
        Long assessmentId = 2L;
        Long processRole = 8L;

        loginPaulPlum();
        AssessmentResource assessmentResource = controller.findById(assessmentId).getSuccessObject();
        assertEquals(AssessmentStates.OPEN, assessmentResource.getAssessmentState());
        assertEquals(processRole, assessmentResource.getProcessRole());

        ApplicationRejectionResource applicationRejection = newApplicationRejectionResource().build();

        RestResult<Void> result = controller.rejectInvitation(assessmentResource.getId(), applicationRejection);
        assertTrue(result.isSuccess());

        RestResult<AssessmentResource> assessmentResult = controller.findById(assessmentId);
        assertEquals(assessmentResult.getErrors().get(0).getErrorKey(), GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION.getErrorKey());

        // Now reject the assessment again
        assertTrue(controller.rejectInvitation(assessmentId, applicationRejection).isFailure());
    }
}
