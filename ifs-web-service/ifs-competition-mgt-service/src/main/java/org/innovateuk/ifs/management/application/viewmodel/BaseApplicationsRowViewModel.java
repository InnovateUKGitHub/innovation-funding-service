package org.innovateuk.ifs.management.application.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Base view model for Competition Management Applications table rows.
 */
abstract class BaseApplicationsRowViewModel {

    private long id;
    private String projectTitle;
    private String lead;

    BaseApplicationsRowViewModel(long id, String projectTitle, String lead) {
        this.id = id;
        this.projectTitle = projectTitle;
        this.lead = lead;
    }

    public long getId() {
        return id;
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
                .append(id, that.id)
                .append(projectTitle, that.projectTitle)
                .append(lead, that.lead)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(projectTitle)
                .append(lead)
                .toHashCode();
    }
}
