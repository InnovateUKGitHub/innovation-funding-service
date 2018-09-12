package org.innovateuk.ifs.assessment.dashboard.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for the applications shown on the Assessor Competition for interview Dashboard.
 */
public class AssessorCompetitionForInterviewDashboardApplicationViewModel {

    private long applicationId;
    private String displayLabel;
    private String leadOrganisation;

    public AssessorCompetitionForInterviewDashboardApplicationViewModel(long applicationId,
                                                                        String displayLabel,
                                                                        String leadOrganisation
    ) {
        this.applicationId = applicationId;
        this.displayLabel = displayLabel;
        this.leadOrganisation = leadOrganisation;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessorCompetitionForInterviewDashboardApplicationViewModel that = (AssessorCompetitionForInterviewDashboardApplicationViewModel) o;

        return new EqualsBuilder()
                .append(applicationId, that.applicationId)
                .append(displayLabel, that.displayLabel)
                .append(leadOrganisation, that.leadOrganisation)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationId)
                .append(displayLabel)
                .append(leadOrganisation)
                .toHashCode();
    }
}
