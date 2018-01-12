package org.innovateuk.ifs.assessment.panel.domain;

import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;
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

    public AssessmentRejectOutcomeValue getRejectReason() {
        return outcome == null ? null : AssessmentRejectOutcomeValue.valueOf(outcome);
    }

    public void setRejectReason(AssessmentRejectOutcomeValue rejectReason) {
        this.outcome = rejectReason == null ? null : rejectReason.name();
    }

    public String getRejectComment() {
        return comment;
    }

    public void setRejectComment(String rejectComment) {
        this.comment = rejectComment;
    }
}