package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for the Inform Ineligible view
 */
public class InformIneligibleViewModel {
    private long competitionId;
    private long applicationId;
    private String competitionName;
    private String applicationName;
    private String leadApplicant;

    public InformIneligibleViewModel(long competitionId, long applicationId, String competitionName, String applicationName, String leadApplicant) {
        this.competitionId = competitionId;
        this.applicationId = applicationId;
        this.competitionName = competitionName;
        this.applicationName = applicationName;
        this.leadApplicant = leadApplicant;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(long competitionId) {
        this.competitionId = competitionId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getLeadApplicant() {
        return leadApplicant;
    }

    public void setLeadApplicant(String leadApplicant) {
        this.leadApplicant = leadApplicant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InformIneligibleViewModel that = (InformIneligibleViewModel) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(applicationId, that.applicationId)
                .append(competitionName, that.competitionName)
                .append(applicationName, that.applicationName)
                .append(leadApplicant, that.leadApplicant)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(applicationId)
                .append(competitionName)
                .append(applicationName)
                .append(leadApplicant)
                .toHashCode();
    }
}
