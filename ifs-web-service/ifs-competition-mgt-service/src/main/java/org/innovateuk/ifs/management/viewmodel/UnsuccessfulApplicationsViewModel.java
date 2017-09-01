package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;

import java.util.List;

/**
 * View model for Competition Management Unsuccessful Applications page
 */
public class UnsuccessfulApplicationsViewModel {

    private List<ApplicationResource> unsuccessfulApplications;

    public List<ApplicationResource> getUnsuccessfulApplications() {
        return unsuccessfulApplications;
    }

    public void setUnsuccessfulApplications(List<ApplicationResource> unsuccessfulApplications) {
        this.unsuccessfulApplications = unsuccessfulApplications;
    }

    public UnsuccessfulApplicationsViewModel(List<ApplicationResource> unsuccessfulApplications) {
        this.unsuccessfulApplications = unsuccessfulApplications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UnsuccessfulApplicationsViewModel that = (UnsuccessfulApplicationsViewModel) o;

        return new EqualsBuilder()
                .append(unsuccessfulApplications, that.unsuccessfulApplications)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(unsuccessfulApplications)
                .toHashCode();
    }
}
