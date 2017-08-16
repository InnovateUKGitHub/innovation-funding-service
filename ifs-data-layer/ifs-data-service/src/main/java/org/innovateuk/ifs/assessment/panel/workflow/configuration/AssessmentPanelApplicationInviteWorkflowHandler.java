package org.innovateuk.ifs.assessment.panel.workflow.configuration;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentPanelApplicationInvite;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentPanelApplicationInviteRejectOutcome;
import org.innovateuk.ifs.assessment.panel.repository.AssessmentPanelApplicationInviteRepository;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelApplicationInviteEvent;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelApplicationInviteState;
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

import static org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelApplicationInviteEvent.*;
import static org.innovateuk.ifs.workflow.domain.ActivityType.ASSESSMENT_PANEL_APPLICATION_INVITE;

/**
 * Manages the process for assigning applications to assessors on an assessment panel.
 */
@Component
public class AssessmentPanelApplicationInviteWorkflowHandler extends BaseWorkflowEventHandler<AssessmentPanelApplicationInvite, AssessmentPanelApplicationInviteState, AssessmentPanelApplicationInviteEvent, Application, ProcessRole> {

    @Autowired
    @Qualifier("assessmentPanelApplicationInviteStateMachine")
    private StateMachine<AssessmentPanelApplicationInviteState, AssessmentPanelApplicationInviteEvent> stateMachine;

    @Autowired
    private AssessmentPanelApplicationInviteRepository assessmentPanelApplicationInviteRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Override
    protected AssessmentPanelApplicationInvite createNewProcess(Application target, ProcessRole participant) {
        return new AssessmentPanelApplicationInvite(target, participant);
    }

    public boolean notifyInvitation(AssessmentPanelApplicationInvite assessmentPanelApplicationInvite) {
        return fireEvent(assessmentPanelApplicationInviteMessage(assessmentPanelApplicationInvite, NOTIFY), assessmentPanelApplicationInvite);
    }

    public boolean rejectInvitation(AssessmentPanelApplicationInvite assessmentPanelApplicationInvite, AssessmentPanelApplicationInviteRejectOutcome rejectOutcome) {
        return fireEvent(rejectMessage(assessmentPanelApplicationInvite, rejectOutcome), assessmentPanelApplicationInvite);
    }

    private static MessageBuilder<AssessmentPanelApplicationInviteEvent> rejectMessage(AssessmentPanelApplicationInvite assessmentPanelApplicationInvite, AssessmentPanelApplicationInviteRejectOutcome ineligibleOutcome) {
        return assessmentPanelApplicationInviteMessage(assessmentPanelApplicationInvite, REJECT)
                .setHeader("rejection", ineligibleOutcome);
    }

    public boolean acceptInvitation(AssessmentPanelApplicationInvite assessmentPanelApplicationInvite) {
        return fireEvent(assessmentPanelApplicationInviteMessage(assessmentPanelApplicationInvite, ACCEPT), assessmentPanelApplicationInvite);
    }

    public boolean markConflictOfInterest(AssessmentPanelApplicationInvite assessmentPanelApplicationInvite) {
        return fireEvent(assessmentPanelApplicationInviteMessage(assessmentPanelApplicationInvite, MARK_CONFLICT_OF_INTEREST), assessmentPanelApplicationInvite);
    }

    public boolean unmarkConflictOfInterest(AssessmentPanelApplicationInvite assessmentPanelApplicationInvite) {
        return fireEvent(assessmentPanelApplicationInviteMessage(assessmentPanelApplicationInvite, UNMARK_CONFLICT_OF_INTEREST), assessmentPanelApplicationInvite);
    }

    @Override
    protected ActivityType getActivityType() {
        return ASSESSMENT_PANEL_APPLICATION_INVITE;
    }

    @Override
    protected ProcessRepository<AssessmentPanelApplicationInvite> getProcessRepository() {
        return assessmentPanelApplicationInviteRepository;
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
    protected StateMachine<AssessmentPanelApplicationInviteState, AssessmentPanelApplicationInviteEvent> getStateMachine() {
        return stateMachine;
    }

    @Override
    protected AssessmentPanelApplicationInvite getOrCreateProcess(Message<AssessmentPanelApplicationInviteEvent> message) {
        return (AssessmentPanelApplicationInvite) message.getHeaders().get("target");
    }


    private static MessageBuilder<AssessmentPanelApplicationInviteEvent> assessmentPanelApplicationInviteMessage(AssessmentPanelApplicationInvite assessmentPanelApplicationInvite, AssessmentPanelApplicationInviteEvent event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", assessmentPanelApplicationInvite);
    }
}