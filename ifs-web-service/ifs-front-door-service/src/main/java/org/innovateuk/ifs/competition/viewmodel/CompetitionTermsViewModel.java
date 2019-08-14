package org.innovateuk.ifs.competition.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CompetitionTermsViewModel {

    private final long competitionId;

    public CompetitionTermsViewModel(long competitionId) {
        this.competitionId = competitionId;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionTermsViewModel that = (CompetitionTermsViewModel) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "CompetitionTermsViewModel{" +
                "competitionId=" + competitionId +
                '}';
    }
}