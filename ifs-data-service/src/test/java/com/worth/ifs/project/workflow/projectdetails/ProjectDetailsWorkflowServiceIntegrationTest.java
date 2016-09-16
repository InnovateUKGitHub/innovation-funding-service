package com.worth.ifs.project.workflow.projectdetails;

import com.worth.ifs.BaseIntegrationTest;
import com.worth.ifs.project.workflow.projectdetails.configuration.ProjectDetailsWorkflowService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ProjectDetailsWorkflowServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ProjectDetailsWorkflowService projectDetailsWorkflowService;

    @Test
    public void testStateChangePendingToRejected() throws Exception {
//        Assessment assessment = assessmentRepository.findOneByParticipantId(PENDING_PROCESS_ROLE);
//        assertEquals(AssessmentStates.PENDING,assessment.getActivityState());
//        assessmentWorkflowEventHandler.rejectInvitation(PENDING_PROCESS_ROLE,assessment,createProcessOutcome());
//        Assessment update = assessmentRepository.findOneByParticipantId(PENDING_PROCESS_ROLE);
//        assertEquals(AssessmentStates.REJECTED,update.getActivityState());
//        assertEquals(COMMENT,update.getLastOutcome().getComment());
//        assertEquals(DESCRIPTION,update.getLastOutcome().getDescription());
    }

//    @Test
//    public void testStateChangeOpenToRejected() throws Exception {
//        Assessment assessment = assessmentRepository.findOneByParticipantId(OPEN_PROCESS_ROLE);
//        assertEquals(AssessmentStates.OPEN,assessment.getActivityState());
//        assessmentWorkflowEventHandler.rejectInvitation(OPEN_PROCESS_ROLE,assessment,createProcessOutcome());
//        Assessment update = assessmentRepository.findOneByParticipantId(OPEN_PROCESS_ROLE);
//        assertEquals(AssessmentStates.REJECTED,update.getActivityState());
//        assertEquals(COMMENT,update.getLastOutcome().getComment());
//        assertEquals(DESCRIPTION,update.getLastOutcome().getDescription());
//    }
//
//    @Test
//    public void testStateChangeOpenToAssessed() throws Exception {
//        Assessment assessment = assessmentRepository.findOneByParticipantId(OPEN_PROCESS_ROLE);
//        assertEquals(AssessmentStates.OPEN,assessment.getActivityState());
//        assessmentWorkflowEventHandler.recommend(OPEN_PROCESS_ROLE,assessment,new ProcessOutcome());
//        Assessment update = assessmentRepository.findOneByParticipantId(OPEN_PROCESS_ROLE);
//        assertEquals(AssessmentStates.ASSESSED,update.getActivityState());
//    }
//
//    @Test
//    public void testStateChangeAssessedToRejected() throws Exception {
//        Assessment assessment = assessmentRepository.findOneByParticipantId(ASSESSED_PROCESS_ROLE);
//        assertEquals(AssessmentStates.ASSESSED,assessment.getActivityState());
//        assessmentWorkflowEventHandler.rejectInvitation(ASSESSED_PROCESS_ROLE,assessment,createProcessOutcome());
//        Assessment update = assessmentRepository.findOneByParticipantId(ASSESSED_PROCESS_ROLE);
//        assertEquals(AssessmentStates.REJECTED,update.getActivityState());
//        assertEquals(COMMENT,update.getLastOutcome(AssessmentOutcomes.REJECT).getComment());
//        assertEquals(DESCRIPTION,update.getLastOutcome(AssessmentOutcomes.REJECT).getDescription());
//    }
//
//    private ProcessOutcome createProcessOutcome() {
//        ProcessOutcome processOutcome = new ProcessOutcome();
//        processOutcome.setComment(COMMENT);
//        processOutcome.setDescription(DESCRIPTION);
//        return processOutcome;
//    }
}
