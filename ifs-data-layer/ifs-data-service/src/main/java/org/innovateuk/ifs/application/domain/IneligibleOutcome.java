package org.innovateuk.ifs.application.domain;

import org.innovateuk.ifs.workflow.domain.ProcessOutcome;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Process outcome for the {@code MARK_INELIGIBLE} application outcome event.
 */
@Entity
@DiscriminatorValue(value = "application-ineligible")
public class IneligibleOutcome extends ProcessOutcome<ApplicationProcess> {

    public String getReason() {
        return description;
    }

    public void setReason(final String reason) {
        this.description = reason;
    }
}
