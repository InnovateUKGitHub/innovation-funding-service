package org.innovateuk.ifs.assessment.domain;

import org.apache.commons.lang3.BooleanUtils;
import org.innovateuk.ifs.workflow.domain.ProcessOutcome;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Process outcome for the {@code FUNDING_DECISION} assessment outcome event.
 */
@Entity
@DiscriminatorValue(value = "funding-decision")
public class AssessmentFundingDecisionOutcome extends ProcessOutcome<Assessment> {

    public void setAssessment(Assessment assessment) {
        setProcess(assessment);
    }

    public Boolean isFundingConfirmation() {
        return BooleanUtils.toBooleanObject(outcome);
    }

    public void setFundingConfirmation(Boolean fundingConfirmation) {
        this.outcome = BooleanUtils.toStringTrueFalse(fundingConfirmation);
    }

    public String getFeedback() {
        return description;
    }

    public void setFeedback(String feedback) {
        this.description = feedback;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}