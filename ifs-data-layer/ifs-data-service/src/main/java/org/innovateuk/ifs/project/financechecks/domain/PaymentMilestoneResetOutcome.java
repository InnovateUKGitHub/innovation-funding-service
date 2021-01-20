package org.innovateuk.ifs.project.financechecks.domain;

import org.innovateuk.ifs.workflow.domain.ProcessOutcome;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "payment-milestone-reset-outcome")
public class PaymentMilestoneResetOutcome extends ProcessOutcome<PaymentMilestoneProcess> {

    public String getReason() {
        return description;
    }

    public void setReason(final String reason) {
        this.description = reason;
    }
}


