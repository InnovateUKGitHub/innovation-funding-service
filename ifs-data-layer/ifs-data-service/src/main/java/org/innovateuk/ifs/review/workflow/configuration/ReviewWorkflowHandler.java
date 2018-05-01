package org.innovateuk.ifs.review.workflow.configuration;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.review.domain.Review;
import org.innovateuk.ifs.review.domain.ReviewRejectOutcome;
import org.innovateuk.ifs.review.repository.ReviewRepository;
import org.innovateuk.ifs.review.resource.ReviewEvent;
import org.innovateuk.ifs.review.resource.ReviewState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.workflow.BaseWorkflowEventHandler;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.review.resource.ReviewEvent.*;

/**
 * Manages the process for assigning applications to assessors on an assessment panel.
 */
@Component
public class ReviewWorkflowHandler extends BaseWorkflowEventHandler<Review, ReviewState, ReviewEvent, Application, ProcessRole> {

    @Autowired
    @Qualifier("assessmentReviewStateMachineFactory")
    private StateMachineFactory<ReviewState, ReviewEvent> stateMachineFactory;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Override
    protected Review createNewProcess(Application target, ProcessRole participant) {
        return new Review(target, participant);
    }

    public boolean notifyInvitation(Review review) {
        return fireEvent(assessmentPanelApplicationInviteMessage(review, NOTIFY), review);
    }

    public boolean rejectInvitation(Review review, ReviewRejectOutcome rejectOutcome) {
        return fireEvent(rejectMessage(review, rejectOutcome), review);
    }

    private static MessageBuilder<ReviewEvent> rejectMessage(Review review, ReviewRejectOutcome ineligibleOutcome) {
        return assessmentPanelApplicationInviteMessage(review, REJECT)
                .setHeader("rejection", ineligibleOutcome);
    }

    public boolean acceptInvitation(Review review) {
        return fireEvent(assessmentPanelApplicationInviteMessage(review, ACCEPT), review);
    }

    public boolean markConflictOfInterest(Review review) {
        return fireEvent(assessmentPanelApplicationInviteMessage(review, MARK_CONFLICT_OF_INTEREST), review);
    }

    public boolean unmarkConflictOfInterest(Review review) {
        return fireEvent(assessmentPanelApplicationInviteMessage(review, UNMARK_CONFLICT_OF_INTEREST), review);
    }

    public boolean withdraw(Review review) {
        return fireEvent(assessmentPanelApplicationInviteMessage(review, WITHDRAW), review);
    }

    @Override
    protected ProcessRepository<Review> getProcessRepository() {
        return reviewRepository;
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
    protected StateMachineFactory<ReviewState, ReviewEvent> getStateMachineFactory() {
        return stateMachineFactory;
    }

    @Override
    protected Review getOrCreateProcess(Message<ReviewEvent> message) {
        return (Review) message.getHeaders().get("target");
    }


    private static MessageBuilder<ReviewEvent> assessmentPanelApplicationInviteMessage(Review review, ReviewEvent event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", review);
    }
}