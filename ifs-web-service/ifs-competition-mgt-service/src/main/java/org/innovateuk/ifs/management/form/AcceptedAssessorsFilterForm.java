package org.innovateuk.ifs.management.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.invite.resource.ParticipantStatusResource;

import java.util.Optional;

import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.ACCEPTED;

/**
 * Form for the list filters on the Invite Assessors Accepted tab.
 */
public class AcceptedAssessorsFilterForm extends BaseBindingResultTarget {

    private Optional<Long> innovationArea = Optional.empty();
    private final ParticipantStatusResource status = ACCEPTED;
    private Optional<Boolean> compliant = Optional.empty();

    public Optional<Long> getInnovationArea() {
        return innovationArea;
    }

    public void setInnovationArea(Optional<Long> innovationArea) {
        this.innovationArea = innovationArea;
    }

    public Optional<ParticipantStatusResource> getStatus() {
        return Optional.of(status);
    }


    public Optional<Boolean> getCompliant() {
        return compliant;
    }

    public void setCompliant(Optional<Boolean> compliant) {
        this.compliant = compliant;
    }
}