package org.innovateuk.ifs.assessment.panel.workflow.configuration;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentReview;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentReviewRejectOutcome;
import org.innovateuk.ifs.assessment.panel.repository.AssessmentReviewRepository;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewEvent;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewState;
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
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewEvent.*;
import static org.innovateuk.ifs.workflow.domain.ActivityType.ASSESSMENT_PANEL_APPLICATION_INVITE;

/**
 * Manages the process for assigning applications to assessors on an assessment panel.
 */
@Component
public class AssessmentReviewWorkflowHandler extends BaseWorkflowEventHandler<AssessmentReview, AssessmentReviewState, AssessmentReviewEvent, Application, ProcessRole> {

    @Autowired
    @Qualifier("assessmentReviewStateMachine")
    private StateMachineFactory<AssessmentReviewState, AssessmentReviewEvent> stateMachine;

    @Autowired
    private AssessmentReviewRepository assessmentReviewRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Override
    protected AssessmentReview createNewProcess(Application target, ProcessRole participant) {
        return new AssessmentReview(target, participant);
    }

    public boolean notifyInvitation(AssessmentReview assessmentReview) {
        return fireEvent(assessmentPanelApplicationInviteMessage(assessmentReview, NOTIFY), assessmentReview);
    }

    public boolean rejectInvitation(AssessmentReview assessmentReview, AssessmentReviewRejectOutcome rejectOutcome) {
        return fireEvent(rejectMessage(assessmentReview, rejectOutcome), assessmentReview);
    }

    private static MessageBuilder<AssessmentReviewEvent> rejectMessage(AssessmentReview assessmentReview, AssessmentReviewRejectOutcome ineligibleOutcome) {
        return assessmentPanelApplicationInviteMessage(assessmentReview, REJECT)
                .setHeader("rejection", ineligibleOutcome);
    }

    public boolean acceptInvitation(AssessmentReview assessmentReview) {
        return fireEvent(assessmentPanelApplicationInviteMessage(assessmentReview, ACCEPT), assessmentReview);
    }

    public boolean markConflictOfInterest(AssessmentReview assessmentReview) {
        return fireEvent(assessmentPanelApplicationInviteMessage(assessmentReview, MARK_CONFLICT_OF_INTEREST), assessmentReview);
    }

    public boolean unmarkConflictOfInterest(AssessmentReview assessmentReview) {
        return fireEvent(assessmentPanelApplicationInviteMessage(assessmentReview, UNMARK_CONFLICT_OF_INTEREST), assessmentReview);
    }

    public boolean withdraw(AssessmentReview assessmentReview) {
        return fireEvent(assessmentPanelApplicationInviteMessage(assessmentReview, WITHDRAW), assessmentReview);
    }

    @Override
    protected ActivityType getActivityType() {
        return ASSESSMENT_PANEL_APPLICATION_INVITE;
    }

    @Override
    protected ProcessRepository<AssessmentReview> getProcessRepository() {
        return assessmentReviewRepository;
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
    protected StateMachine<AssessmentReviewState, AssessmentReviewEvent> getStateMachine() {
        return stateMachine.getStateMachine();
    }

    @Override
    protected AssessmentReview getOrCreateProcess(Message<AssessmentReviewEvent> message) {
        return (AssessmentReview) message.getHeaders().get("target");
    }


    private static MessageBuilder<AssessmentReviewEvent> assessmentPanelApplicationInviteMessage(AssessmentReview assessmentReview, AssessmentReviewEvent event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", assessmentReview);
    }
}