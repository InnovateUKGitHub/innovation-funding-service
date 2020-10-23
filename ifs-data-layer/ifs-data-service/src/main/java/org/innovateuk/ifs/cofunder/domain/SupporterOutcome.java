package org.innovateuk.ifs.supporter.domain;

import org.apache.commons.lang3.BooleanUtils;
import org.innovateuk.ifs.workflow.domain.ProcessOutcome;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "supporter-outcome")
public class SupporterOutcome extends ProcessOutcome<SupporterAssignment> {
    public SupporterOutcome() {
    }

    public SupporterOutcome(boolean decision, String comment) {
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