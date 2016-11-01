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

import java.util.Optional;

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

    public boolean rejectInvitation(Long processRoleId, Assessment assessment, ApplicationRejectionResource applicationRejection) {
        return stateHandler.handleEventWithState(MessageBuilder
                .withPayload(REJECT)
                .setHeader("target", assessment.getTarget())
                .setHeader("participant", assessment.getParticipant())
                .setHeader("processRoleId", processRoleId)
                .setHeader("processOutcome", new ProcessOutcome(null, applicationRejection.getRejectReason(), applicationRejection.getRejectComment()))
                .build(), assessment.getActivityState());
    }

    public boolean recommend(Long processRoleId, Assessment assessment, AssessmentFundingDecisionResource assessmentFundingDecision) {
        return stateHandler.handleEventWithState(MessageBuilder
                .withPayload(RECOMMEND)
                .setHeader("assessment", assessment)
                .setHeader("target", assessment.getTarget())
                .setHeader("participant", assessment.getParticipant())
                .setHeader("processRoleId", processRoleId)
                .setHeader("processOutcome", new ProcessOutcome(ofNullable(assessmentFundingDecision.getFundingConfirmation()).map(BooleanUtils::toStringYesNo).orElse(null), assessmentFundingDecision.getFeedback(), assessmentFundingDecision.getComment()))
                .build(), assessment.getActivityState());
    }

    public boolean acceptInvitation(Long processRoleId, Assessment assessment) {
        return stateHandler.handleEventWithState(MessageBuilder
                .withPayload(ACCEPT)
                .setHeader("target", assessment.getTarget())
                .setHeader("participant", assessment.getParticipant())
                .setHeader("assessment", assessment)
                .setHeader("processRoleId", processRoleId)
                .setHeader("processOutcome", new ProcessOutcome())
                .build(), assessment.getActivityState());
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

        Application target = (Application) message.getHeaders().get("target");
        ProcessRole participant = (ProcessRole) message.getHeaders().get("participant");

        Optional<Assessment> existingProcess = Optional.ofNullable(getProcessByParticipantId(participant.getId()));
        Assessment processToUpdate = existingProcess.orElseGet(() -> createNewProcess(target, participant));

        return processToUpdate;
    }
}
