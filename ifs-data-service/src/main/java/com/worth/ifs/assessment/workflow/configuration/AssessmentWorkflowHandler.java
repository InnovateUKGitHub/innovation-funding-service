package com.worth.ifs.assessment.workflow.configuration;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.assessment.resource.ApplicationRejectionResource;
import com.worth.ifs.assessment.resource.AssessmentFundingDecisionResource;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.workflow.BaseWorkflowEventHandler;
import com.worth.ifs.workflow.domain.ActivityType;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.repository.ProcessRepository;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import static com.worth.ifs.assessment.resource.AssessmentOutcomes.*;
import static com.worth.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static java.util.Optional.ofNullable;

/**
 * {@code AssessmentWorkflowService} is the entry point for triggering the workflow.
 * Based on the assessment's current state the next one is tried to transition to by triggering
 * an event.
 */
@Component
public class AssessmentWorkflowHandler extends BaseWorkflowEventHandler<Assessment, AssessmentStates, AssessmentOutcomes, Application, ProcessRole> {

    @Autowired
    @Qualifier("assessmentStateMachine")
    private StateMachine<AssessmentStates, AssessmentOutcomes> stateMachine;

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

    public boolean rejectInvitation(Assessment assessment, ApplicationRejectionResource applicationRejection) {
        return fireEvent(assessmentEventWithOutcome(assessment, rejectInvitationOutcome(applicationRejection), REJECT), assessment);
    }

    public boolean acceptInvitation(Assessment assessment) {
        return fireEvent(assessmentEvent(assessment, ACCEPT), assessment);
    }

    public boolean feedback(Assessment assessment) {
        return fireEvent(assessmentEvent(assessment, FEEDBACK), assessment);
    }

    public boolean fundingDecision(Assessment assessment, AssessmentFundingDecisionResource assessmentFundingDecision) {
        return fireEvent(assessmentEventWithOutcome(assessment, fundingDecisionOutcome(assessmentFundingDecision), FUNDING_DECISION), assessment);
    }

    public boolean submit(Assessment assessment) {
        return fireEvent(assessmentEvent(assessment, SUBMIT), assessment);
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
    protected StateMachine<AssessmentStates, AssessmentOutcomes> getStateMachine() {
        return stateMachine;
    }

    @Override
    protected Assessment getOrCreateProcess(Message<AssessmentOutcomes> message) {
        return (Assessment) message.getHeaders().get("assessment");
    }

    private MessageBuilder<AssessmentOutcomes> assessmentEventWithOutcome(Assessment assessment, ProcessOutcome processOutcome, AssessmentOutcomes event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("assessment", assessment)
                .setHeader("processOutcome", processOutcome);
    }

    private MessageBuilder<AssessmentOutcomes> assessmentEvent(Assessment assessment, AssessmentOutcomes event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("assessment", assessment);
    }

    private ProcessOutcome rejectInvitationOutcome(ApplicationRejectionResource applicationRejection) {
        ProcessOutcome processOutcome = new ProcessOutcome();
        processOutcome.setDescription(applicationRejection.getRejectReason());
        processOutcome.setComment(applicationRejection.getRejectComment());
        return processOutcome;
    }

    private ProcessOutcome fundingDecisionOutcome(AssessmentFundingDecisionResource assessmentFundingDecision) {
        ProcessOutcome processOutcome = new ProcessOutcome();
        processOutcome.setOutcome(ofNullable(assessmentFundingDecision.getFundingConfirmation()).map(BooleanUtils::toStringYesNo).orElse(null));
        processOutcome.setDescription(assessmentFundingDecision.getFeedback());
        processOutcome.setComment(assessmentFundingDecision.getComment());
        return processOutcome;
    }
}
