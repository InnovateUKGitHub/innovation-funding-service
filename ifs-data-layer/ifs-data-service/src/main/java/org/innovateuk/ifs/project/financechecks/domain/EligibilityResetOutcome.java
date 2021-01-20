package org.innovateuk.ifs.project.financechecks.domain;

import org.innovateuk.ifs.workflow.domain.ProcessOutcome;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "eligibility-reset-outcome")
public class EligibilityResetOutcome extends ProcessOutcome<EligibilityProcess> {

    public String getReason() {
        return description;
    }

    public void setReason(final String reason) {
        this.description = reason;
    }
}

