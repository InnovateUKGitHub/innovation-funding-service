package org.innovateuk.ifs.assessment.workflow.configuration;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.domain.AssessmentRejectOutcome;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentFundingDecisionOutcome;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentEvent;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.workflow.BaseWorkflowEventHandler;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.assessment.resource.AssessmentEvent.*;
import static org.innovateuk.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;

/**
 * {@code AssessmentWorkflowService} is the entry point for triggering the workflow.
 * Based on the assessment's current state the next one is tried to transition to by triggering
 * an event.
 */
@Component
public class AssessmentWorkflowHandler extends BaseWorkflowEventHandler<Assessment, AssessmentState, AssessmentEvent, Application, ProcessRole> {

    @Autowired
    @Qualifier("assessmentStateMachine")
    private StateMachine<AssessmentState, AssessmentEvent> stateMachine;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Override
    protected Assessment createNewProcess(Application target, ProcessRole participant) {
        return new Assessment(target, participant);
    }

    public boolean rejectInvitation(Assessment assessment, AssessmentRejectOutcome assessmentRejectOutcome) {
        return fireEvent(rejectMessage(assessment, assessmentRejectOutcome), assessment);
    }

    public boolean acceptInvitation(Assessment assessment) {
        return fireEvent(assessmentMessage(assessment, ACCEPT), assessment);
    }

    public boolean feedback(Assessment assessment) {
        return fireEvent(assessmentMessage(assessment, FEEDBACK), assessment);
    }

    public boolean fundingDecision(Assessment assessment, AssessmentFundingDecisionOutcome fundingDecision) {
        return fireEvent(fundingDecisionMessage(assessment, fundingDecision), assessment);
    }

    public boolean withdraw(Assessment assessment) {
        return fireEvent(assessmentMessage(assessment, WITHDRAW), assessment);
    }

    public boolean submit(Assessment assessment) {
        return fireEvent(assessmentMessage(assessment, SUBMIT), assessment);
    }

    public boolean notify(Assessment assessment) {
        return fireEvent(assessmentMessage(assessment, NOTIFY), assessment);
    }

    @Override
    protected ActivityType getActivityType() {
        return APPLICATION_ASSESSMENT;
    }

    @Override
    protected ProcessRepository<Assessment> getProcessRepository() {
        return assessmentRepository;
    }

    @Override
    protected CrudRepository<Application, Long> getTargetRepository() {
        return applicationRepository;
    }

    @Override
    protected CrudRepository<ProcessRole, Long> getParticipantRepository() {
        return processRoleRepository;
    }

    @Override
    protected StateMachine<AssessmentState, AssessmentEvent> getStateMachine() {
        return stateMachine;
    }

    @Override
    protected Assessment getOrCreateProcess(Message<AssessmentEvent> message) {
        return (Assessment) message.getHeaders().get("target");
    }

    private MessageBuilder<AssessmentEvent> fundingDecisionMessage(Assessment assessment, AssessmentFundingDecisionOutcome fundingDecision) {
        return assessmentMessage(assessment, FUNDING_DECISION)
                .setHeader("fundingDecision", fundingDecision);
    }

    private MessageBuilder<AssessmentEvent> rejectMessage(Assessment assessment, AssessmentRejectOutcome rejection) {
        return assessmentMessage(assessment, REJECT)
                .setHeader("rejection", rejection);
    }

    private MessageBuilder<AssessmentEvent> assessmentMessage(Assessment assessment, AssessmentEvent event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", assessment);
    }
}
