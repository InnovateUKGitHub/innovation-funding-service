package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Base view model for Competition Management Applications table rows.
 */
abstract class BaseApplicationsRowViewModel {

    private long applicationNumber;
    private String projectTitle;
    private String lead;

    BaseApplicationsRowViewModel(long applicationNumber, String projectTitle, String lead) {
        this.applicationNumber = applicationNumber;
        this.projectTitle = projectTitle;
        this.lead = lead;
    }

    public long getApplicationNumber() {
        return applicationNumber;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public String getLead() {
        return lead;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BaseApplicationsRowViewModel that = (BaseApplicationsRowViewModel) o;

        return new EqualsBuilder()
                .append(applicationNumber, that.applicationNumber)
                .append(projectTitle, that.projectTitle)
                .append(lead, that.lead)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationNumber)
                .append(projectTitle)
                .append(lead)
                .toHashCode();
    }
}
