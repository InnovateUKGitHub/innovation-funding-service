package org.innovateuk.ifs.cofunder.domain;

import org.apache.commons.lang3.BooleanUtils;
import org.innovateuk.ifs.workflow.domain.ProcessOutcome;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "cofunder-outcome")
public class CofunderOutcome extends ProcessOutcome<CofunderAssignment> {
    public CofunderOutcome() {
    }

    public CofunderOutcome(boolean decision, String comment) {
        setFundingConfirmation(decision);
        setComment(comment);
    }

    public Boolean isFundingConfirmation() {
        return BooleanUtils.toBooleanObject(outcome);
    }

    public void setFundingConfirmation(Boolean fundingConfirmation) {
        this.outcome = BooleanUtils.toStringTrueFalse(fundingConfirmation);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}