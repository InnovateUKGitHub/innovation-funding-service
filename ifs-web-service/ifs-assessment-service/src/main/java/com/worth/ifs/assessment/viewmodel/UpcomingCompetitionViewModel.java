package com.worth.ifs.assessment.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * ViewModel of an UpcomingCompetition.
 */
public class UpcomingCompetitionViewModel {

    private String competitionName;

    public UpcomingCompetitionViewModel(String competitionName) {
        this.competitionName = competitionName;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UpcomingCompetitionViewModel that = (UpcomingCompetitionViewModel) o;

        return new EqualsBuilder()
                .append(competitionName, that.competitionName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionName)
                .toHashCode();
    }
}
