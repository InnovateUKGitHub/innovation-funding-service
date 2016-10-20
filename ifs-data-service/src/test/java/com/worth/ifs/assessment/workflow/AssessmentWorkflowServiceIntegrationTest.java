package com.worth.ifs.assessment.workflow;

import com.worth.ifs.assessment.resource.AssessmentFundingDecisionResource;
import com.worth.ifs.commons.BaseIntegrationTest;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@Transactional
public class AssessmentWorkflowServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AssessmentWorkflowHandler assessmentWorkflowService;

    @Autowired
    private AssessmentRepository assessmentRepository;

    private static final long PENDING_PROCESS_ROLE = 17L;
    private static final long OPEN_PROCESS_ROLE = 8L;
    private static final long ASSESSED_PROCESS_ROLE = 16L;

    private static final String COMMENT = "comment";
    private static final String DESCRIPTION = "description";

    @Test
    public void testStateChangePendingToRejected() throws Exception {
        Assessment assessment = assessmentRepository.findOneByParticipantId(PENDING_PROCESS_ROLE);
        assertEquals(AssessmentStates.PENDING,assessment.getActivityState());
        assessmentWorkflowService.rejectInvitation(PENDING_PROCESS_ROLE,assessment,createProcessOutcome());
        Assessment update = assessmentRepository.findOneByParticipantId(PENDING_PROCESS_ROLE);
        assertEquals(AssessmentStates.REJECTED,update.getActivityState());
        assertEquals(COMMENT,update.getLastOutcome().getComment());
        assertEquals(DESCRIPTION,update.getLastOutcome().getDescription());
    }

    @Test
    public void testStateChangeOpenToRejected() throws Exception {
        Assessment assessment = assessmentRepository.findOneByParticipantId(OPEN_PROCESS_ROLE);
        assertEquals(AssessmentStates.OPEN,assessment.getActivityState());
        assessmentWorkflowService.rejectInvitation(OPEN_PROCESS_ROLE,assessment,createProcessOutcome());
        Assessment update = assessmentRepository.findOneByParticipantId(OPEN_PROCESS_ROLE);
        assertEquals(AssessmentStates.REJECTED,update.getActivityState());
        assertEquals(COMMENT,update.getLastOutcome().getComment());
        assertEquals(DESCRIPTION,update.getLastOutcome().getDescription());
    }

    @Test
    public void testStateChangeOpenToAssessed() throws Exception {
        Assessment assessment = assessmentRepository.findOneByParticipantId(OPEN_PROCESS_ROLE);
        assertEquals(AssessmentStates.OPEN,assessment.getActivityState());
        assessmentWorkflowService.recommend(OPEN_PROCESS_ROLE,assessment, new AssessmentFundingDecisionResource());
        Assessment update = assessmentRepository.findOneByParticipantId(OPEN_PROCESS_ROLE);
        assertEquals(AssessmentStates.ASSESSED,update.getActivityState());
    }

    @Test
    public void testStateChangeAssessedToRejected() throws Exception {
        Assessment assessment = assessmentRepository.findOneByParticipantId(ASSESSED_PROCESS_ROLE);
        assertEquals(AssessmentStates.ASSESSED,assessment.getActivityState());
        assessmentWorkflowService.rejectInvitation(ASSESSED_PROCESS_ROLE,assessment,createProcessOutcome());
        Assessment update = assessmentRepository.findOneByParticipantId(ASSESSED_PROCESS_ROLE);
        assertEquals(AssessmentStates.REJECTED,update.getActivityState());
        assertEquals(COMMENT,update.getLastOutcome(AssessmentOutcomes.REJECT).getComment());
        assertEquals(DESCRIPTION,update.getLastOutcome(AssessmentOutcomes.REJECT).getDescription());
    }

    private ProcessOutcome createProcessOutcome() {
        ProcessOutcome processOutcome = new ProcessOutcome();
        processOutcome.setComment(COMMENT);
        processOutcome.setDescription(DESCRIPTION);
        return processOutcome;
    }
}
