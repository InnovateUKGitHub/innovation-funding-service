package org.innovateuk.ifs.project.financechecks.domain;

import org.innovateuk.ifs.workflow.domain.ProcessOutcome;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "viability-reset-outcome")
public class ViabilityResetOutcome extends ProcessOutcome<ViabilityProcess> {

    public String getReason() {
        return description;
    }

    public void setReason(final String reason) {
        this.description = reason;
    }
}

