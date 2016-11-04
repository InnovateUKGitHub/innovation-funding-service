package com.worth.ifs.assessment.workflow;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.assessment.resource.ApplicationRejectionResource;
import com.worth.ifs.assessment.resource.AssessmentFundingDecisionResource;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import com.worth.ifs.commons.BaseIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.worth.ifs.assessment.builder.ApplicationRejectionResourceBuilder.newApplicationRejectionResource;
import static com.worth.ifs.assessment.builder.AssessmentFundingDecisionResourceBuilder.newAssessmentFundingDecisionResource;
import static com.worth.ifs.assessment.resource.AssessmentOutcomes.FUNDING_DECISION;
import static com.worth.ifs.assessment.resource.AssessmentOutcomes.REJECT;
import static com.worth.ifs.assessment.resource.AssessmentStates.*;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Transactional
public class AssessmentWorkflowHandlerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AssessmentWorkflowHandler assessmentWorkflowHandler;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Test
    public void rejectInvitation_pendingToRejected() throws Exception {
        long pendingAssessmentId = 4L;

        Assessment assessment = assessmentRepository.findOne(pendingAssessmentId);
        assertEquals(PENDING, assessment.getActivityState());

        assertTrue(assessmentWorkflowHandler.rejectInvitation(assessment, createRejection()));

        assertAssessmentRejectedAfterRejection(pendingAssessmentId);
    }

    @Test
    public void acceptInvitation_pendingToAccepted() throws Exception {
        long pendingAssessmentId = 4L;

        Assessment assessment = assessmentRepository.findOne(pendingAssessmentId);
        assertEquals(PENDING, assessment.getActivityState());

        assertTrue(assessmentWorkflowHandler.acceptInvitation(assessment));

        assertAssessmentState(pendingAssessmentId, ACCEPTED);
    }

    @Test
    public void rejectInvitation_acceptedToRejected() throws Exception {
        long acceptedAssessmentId = 8L;

        Assessment assessment = assessmentRepository.findOne(acceptedAssessmentId);
        assertEquals(ACCEPTED, assessment.getActivityState());

        assertTrue(assessmentWorkflowHandler.rejectInvitation(assessment, createRejection()));

        assertAssessmentRejectedAfterRejection(acceptedAssessmentId);
    }

    @Test
    public void feedback_acceptedToOpen() throws Exception {
        long acceptedAssessmentId = 8L;

        Assessment assessment = assessmentRepository.findOne(acceptedAssessmentId);
        assertEquals(ACCEPTED, assessment.getActivityState());

        assertTrue(assessmentWorkflowHandler.feedback(assessment));

        assertAssessmentState(acceptedAssessmentId, OPEN);
    }

    @Test
    public void fundingDecision_acceptedToOpen() throws Exception {
        long acceptedAssessmentId = 8L;

        Assessment assessment = assessmentRepository.findOne(acceptedAssessmentId);
        assertEquals(ACCEPTED, assessment.getActivityState());

        assertTrue(assessmentWorkflowHandler.fundingDecision(assessment, createFundingDecision()));

        assertAssessmentOpenAfterFundingDecision(acceptedAssessmentId);
    }

    @Test
    public void rejectInvitation_openToRejected() throws Exception {
        long openAssessmentId = 2L;

        Assessment assessment = assessmentRepository.findOne(openAssessmentId);
        assertEquals(OPEN, assessment.getActivityState());

        assertTrue(assessmentWorkflowHandler.rejectInvitation(assessment, createRejection()));

        assertAssessmentRejectedAfterRejection(openAssessmentId);
    }

    @Test
    public void feedback_openToOpen() throws Exception {
        long openAssessmentId = 2L;

        Assessment assessment = assessmentRepository.findOne(openAssessmentId);
        assertEquals(OPEN, assessment.getActivityState());

        assertTrue(assessmentWorkflowHandler.feedback(assessment));

        assertAssessmentState(openAssessmentId, OPEN);
    }

    @Test
    public void fundingDecision_openToOpen() throws Exception {
        long openAssessmentId = 2L;

        Assessment assessment = assessmentRepository.findOne(openAssessmentId);
        assertEquals(OPEN, assessment.getActivityState());

        assertTrue(assessmentWorkflowHandler.fundingDecision(assessment, createFundingDecision()));

        assertAssessmentOpenAfterFundingDecision(openAssessmentId);
    }

    @Test
    public void rejectInvitation_readyToSubmitToRejected() throws Exception {
        long readyToSubmitAssessmentId = 3L;

        Assessment assessment = assessmentRepository.findOne(readyToSubmitAssessmentId);
        assertEquals(READY_TO_SUBMIT, assessment.getActivityState());

        assertTrue(assessmentWorkflowHandler.rejectInvitation(assessment, createRejection()));

        assertAssessmentRejectedAfterRejection(readyToSubmitAssessmentId);
    }

    @Test
    public void feedback_readyToSubmitToOpen() throws Exception {
        long readyToSubmitAssessmentId = 3L;

        Assessment assessment = assessmentRepository.findOne(readyToSubmitAssessmentId);
        assertEquals(READY_TO_SUBMIT, assessment.getActivityState());

        assertTrue(assessmentWorkflowHandler.feedback(assessment));

        assertAssessmentState(readyToSubmitAssessmentId, OPEN);
    }

    @Test
    public void fundingDecision_readyToSubmitToOpen() throws Exception {
        long readyToSubmitAssessmentId = 3L;

        Assessment assessment = assessmentRepository.findOne(readyToSubmitAssessmentId);
        assertEquals(READY_TO_SUBMIT, assessment.getActivityState());

        assertTrue(assessmentWorkflowHandler.fundingDecision(assessment, createFundingDecision()));

        assertAssessmentOpenAfterFundingDecision(readyToSubmitAssessmentId);
    }

    @Test
    public void submit_readyToSubmitToSubmitted() throws Exception {
        long readyToSubmitAssessmentId = 3L;

        Assessment assessment = assessmentRepository.findOne(readyToSubmitAssessmentId);
        assertEquals(READY_TO_SUBMIT, assessment.getActivityState());

        assertTrue(assessmentWorkflowHandler.submit(assessment));

        assertAssessmentState(readyToSubmitAssessmentId, SUBMITTED);
    }

    private AssessmentFundingDecisionResource createFundingDecision() {
        return newAssessmentFundingDecisionResource()
                .withFundingConfirmation(TRUE)
                .withComment("comment")
                .withFeedback("feedback")
                .build();
    }

    private ApplicationRejectionResource createRejection() {
        return newApplicationRejectionResource()
                .withRejectReason("reason")
                .withRejectComment("comment")
                .build();
    }

    private void assertAssessmentState(long assessmentId, AssessmentStates expectedState) {
        Assessment updated = assessmentRepository.findOne(assessmentId);
        assertEquals(expectedState, updated.getActivityState());
    }

    private void assertAssessmentOpenAfterFundingDecision(long assessmentId) {
        Assessment updated = assessmentRepository.findOne(assessmentId);
        assertEquals(OPEN, updated.getActivityState());
        assertEquals("yes", updated.getLastOutcome(FUNDING_DECISION).getOutcome());
        assertEquals("comment", updated.getLastOutcome(FUNDING_DECISION).getComment());
        assertEquals("feedback", updated.getLastOutcome(FUNDING_DECISION).getDescription());
    }

    private void assertAssessmentRejectedAfterRejection(long assessmentId) {
        Assessment updated = assessmentRepository.findOne(assessmentId);
        assertEquals(REJECTED, updated.getActivityState());
        assertEquals("comment", updated.getLastOutcome(REJECT).getComment());
        assertEquals("reason", updated.getLastOutcome(REJECT).getDescription());
    }
}
