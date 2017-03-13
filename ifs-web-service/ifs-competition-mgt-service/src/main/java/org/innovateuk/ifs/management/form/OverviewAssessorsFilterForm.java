package org.innovateuk.ifs.management.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.invite.resource.ParticipantStatusResource;

import java.util.Optional;

/**
 * Form for the list filters on the Invite Assessors Overview tab.
 */
public class OverviewAssessorsFilterForm extends BaseBindingResultTarget {

    private Optional<Long> innovationArea = Optional.empty();
    private Optional<ParticipantStatusResource> status = Optional.empty();
    private Optional<Boolean> contract = Optional.empty();

    public Optional<Long> getInnovationArea() {
        return innovationArea;
    }

    public void setInnovationArea(Optional<Long> innovationArea) {
        this.innovationArea = innovationArea;
    }

    public Optional<ParticipantStatusResource> getStatus() {
        return status;
    }

    public void setStatus(Optional<ParticipantStatusResource> status) {
        this.status = status;
    }

    public Optional<Boolean> getContract() {
        return contract;
    }

    public void setContract(Optional<Boolean> contract) {
        this.contract = contract;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OverviewAssessorsFilterForm that = (OverviewAssessorsFilterForm) o;

        return new EqualsBuilder()
                .append(innovationArea, that.innovationArea)
                .append(status, that.status)
                .append(contract, that.contract)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(innovationArea)
                .append(status)
                .append(contract)
                .toHashCode();
    }
}
