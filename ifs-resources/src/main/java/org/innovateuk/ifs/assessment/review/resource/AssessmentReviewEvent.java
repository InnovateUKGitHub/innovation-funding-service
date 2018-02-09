package org.innovateuk.ifs.assessment.review.resource;

import org.innovateuk.ifs.workflow.resource.ProcessEvent;

/**
 * Events that can happen during the AssessmentReview workflow.
 */
public enum AssessmentReviewEvent implements ProcessEvent {
    NOTIFY("notify"),
    ACCEPT("accept"),
    REJECT("reject"),
    MARK_CONFLICT_OF_INTEREST("mark_conflict_of_interest"),
    UNMARK_CONFLICT_OF_INTEREST("unmark_conflict_of_interest"),
    WITHDRAW("withdraw");

    String event;

    AssessmentReviewEvent(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}