package org.innovateuk.ifs.review.domain;

import org.innovateuk.ifs.workflow.domain.ProcessOutcome;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


/**
 * Process outcome for the {@code Review}s {@code REJECT} event.
 */
@Entity
@DiscriminatorValue(value = "assessment-panel-application-invite-reject")
public class ReviewRejectOutcome extends ProcessOutcome<Review> {

    public void setAssessmentPanelApplicationInvite(Review review) {
        setProcess(review);
    }

    public String getRejectReason() {
        return comment;
    }

    public void setRejectReason(String rejectComment) {
        this.comment = rejectComment;
    }
}