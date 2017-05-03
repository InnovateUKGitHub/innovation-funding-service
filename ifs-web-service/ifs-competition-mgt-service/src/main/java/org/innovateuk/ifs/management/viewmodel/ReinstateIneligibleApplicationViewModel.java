package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * View model for the Competition Management Reinstate Ineligible Application view.
 */
public class ReinstateIneligibleApplicationViewModel {

    private final long competitionId;
    private final long applicationId;
    private final String applicationName;

    public ReinstateIneligibleApplicationViewModel(final long competitionId, final long applicationId, final String applicationName) {
        this.competitionId = competitionId;
        this.applicationId = applicationId;
        this.applicationName = applicationName;
    }

    public long getCompetitionId() {
        return competitionId;
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

        ReinstateIneligibleApplicationViewModel that = (ReinstateIneligibleApplicationViewModel) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(applicationId, that.applicationId)
                .append(applicationName, that.applicationName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(applicationId)
                .append(applicationName)
                .toHashCode();
    }
}
