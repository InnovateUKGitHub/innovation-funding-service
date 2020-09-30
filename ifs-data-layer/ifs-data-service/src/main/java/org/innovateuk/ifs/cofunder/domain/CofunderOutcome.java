package org.innovateuk.ifs.cofunder.domain;

import org.apache.commons.lang3.BooleanUtils;
import org.innovateuk.ifs.workflow.domain.ProcessOutcome;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Process outcome for the {@code FUNDING_DECISION} assessment outcome event.
 */
@Entity
@DiscriminatorValue(value = "funding-decision")
public class CofunderOutcome extends ProcessOutcome<CofunderAssignment> {

    public void setAssessment(CofunderAssignment assessment) {
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