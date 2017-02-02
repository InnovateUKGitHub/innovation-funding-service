package org.innovateuk.ifs.assessment.domain;

import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;
import org.innovateuk.ifs.workflow.domain.ProcessOutcome;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *  Process outcome for the {@code REJECT} assessment outcome event.
 */
@Entity
@DiscriminatorValue(value = "reject")
public class AssessmentRejectOutcome extends ProcessOutcome<Assessment> {

    public void setAssessment(Assessment assessment) {
        setProcess(assessment);
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