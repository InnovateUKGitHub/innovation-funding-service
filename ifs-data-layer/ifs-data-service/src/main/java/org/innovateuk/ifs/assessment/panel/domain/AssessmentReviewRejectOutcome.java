package org.innovateuk.ifs.assessment.panel.domain;

import org.innovateuk.ifs.workflow.domain.ProcessOutcome;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


/**
 * Process outcome for the {@code }AssessmentReview}s {@code REJECT} event.
 */
@Entity
@DiscriminatorValue(value = "assessment-panel-application-invite-reject")
public class AssessmentReviewRejectOutcome extends ProcessOutcome<AssessmentReview> {

    public void setAssessmentPanelApplicationInvite(AssessmentReview assessmentReview) {
        setProcess(assessmentReview);
    }

    public String getRejectionComment() {
        return comment;
    }

    public void setRejectionComment(String rejectComment) {
        this.comment = rejectComment;
    }
}