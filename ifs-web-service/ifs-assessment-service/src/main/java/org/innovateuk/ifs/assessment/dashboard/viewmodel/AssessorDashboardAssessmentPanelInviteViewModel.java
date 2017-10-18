package org.innovateuk.ifs.assessment.dashboard.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;

public class AssessorDashboardAssessmentPanelInviteViewModel {

    private final String hash;
    private final String competitionName;
    private final long competitionId;

    public AssessorDashboardAssessmentPanelInviteViewModel(
            String hash,
            String competitionName,
            long competitionId
    ) {
        this.hash = hash;
        this.competitionName = competitionName;
        this.competitionId = competitionId;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getInviteHash() {
        return hash;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorDashboardAssessmentPanelInviteViewModel that = (org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorDashboardAssessmentPanelInviteViewModel) o;

        return new EqualsBuilder()
                .append(hash, that.hash)
                .append(competitionName, that.competitionName)
                .append(competitionId, that.competitionId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(hash)
                .append(competitionName)
                .append(competitionId)
                .toHashCode();
    }
}
