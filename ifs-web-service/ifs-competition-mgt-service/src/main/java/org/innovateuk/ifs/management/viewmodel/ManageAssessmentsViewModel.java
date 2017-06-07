package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

/**
 * View model for the 'Manage assessments' dashboard page.
 *
 * Contains statistics and links to other pages such as 'Manage applications' and 'Manage assessors'.
 */
public class ManageAssessmentsViewModel {

    private final long competitionId;
    private final String competitionName;
    private final boolean inAssessment;

    public ManageAssessmentsViewModel(CompetitionResource competition) {
        this.competitionId = competition.getId();
        this.competitionName = competition.getName();
        this.inAssessment = competition.getCompetitionStatus() == CompetitionStatus.IN_ASSESSMENT;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public boolean isInAssessment() {
        return inAssessment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ManageAssessmentsViewModel that = (ManageAssessmentsViewModel) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .append(inAssessment, that.inAssessment)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(competitionName)
                .append(inAssessment)
                .toHashCode();
    }
}
