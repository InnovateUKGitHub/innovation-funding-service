package org.innovateuk.ifs.application.team.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for the Add Organisation view.
 */
public class ApplicationTeamAddOrganisationViewModel {

    private long applicationId;
    private String applicationName;

    public ApplicationTeamAddOrganisationViewModel(long applicationId, String applicationName) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ApplicationTeamAddOrganisationViewModel that = (ApplicationTeamAddOrganisationViewModel) o;

        return new EqualsBuilder()
                .append(applicationId, that.applicationId)
                .append(applicationName, that.applicationName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationId)
                .append(applicationName)
                .toHashCode();
    }
}