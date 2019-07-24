package org.innovateuk.ifs.management.assessor.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.invite.resource.ParticipantStatusResource;

import java.util.Optional;

/**
 * Form for the list filters on the Invite Assessors Overview tab.
 */
public class OverviewAssessorsFilterForm extends BaseBindingResultTarget {

    private Optional<ParticipantStatusResource> status = Optional.empty();
    private Optional<Boolean> compliant = Optional.empty();

    public Optional<ParticipantStatusResource> getStatus() {
        return status;
    }

    public void setStatus(Optional<ParticipantStatusResource> status) {
        this.status = status;
    }

    public Optional<Boolean> getCompliant() {
        return compliant;
    }

    public void setCompliant(Optional<Boolean> compliant) {
        this.compliant = compliant;
    }

    public boolean anyFilterIsActive() {
        return this.status.isPresent() || this.compliant.isPresent();
    }
}
