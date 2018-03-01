package org.innovateuk.ifs.review.workflow.actions;

import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflow;
import org.innovateuk.ifs.review.domain.Review;
import org.innovateuk.ifs.review.domain.ReviewRejectOutcome;
import org.innovateuk.ifs.review.resource.ReviewEvent;
import org.innovateuk.ifs.review.resource.ReviewState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

/**
 * The {@code ReviewRejectAction} handles the reject event for an {@code Review}
 * For more info see {@link AssessmentWorkflow}
 */
@Component
public class ReviewRejectAction implements Action<ReviewState, ReviewEvent> {
    @Override
    public void execute(StateContext<ReviewState, ReviewEvent> context) {
        Review invite = (Review) context.getMessageHeader("target");
        ReviewRejectOutcome rejectOutcome = (ReviewRejectOutcome) context.getMessageHeader("rejection");
        invite.setRejection(rejectOutcome);
    }
}