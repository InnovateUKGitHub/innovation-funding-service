package org.innovateuk.ifs.project.status.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionPendingSpendProfilesResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.util.List;

/**
 * View model to support the view for displaying projects for which Spend Profile generation is pending, for a given competition
 */
public class CompetitionPendingSpendProfilesViewModel {

    private long competitionId;

    private String competitionName;

    private List<CompetitionPendingSpendProfilesResource> pendingSpendProfiles;

    private long openQueryCount;

    private long pendingSpendProfilesCount;

    private boolean showTabs;

    public CompetitionPendingSpendProfilesViewModel() {
    }

    public CompetitionPendingSpendProfilesViewModel(CompetitionResource competition, List<CompetitionPendingSpendProfilesResource> pendingSpendProfiles,
                                                    long openQueryCount, long pendingSpendProfilesCount, boolean showTabs) {
        this.competitionId = competition.getId();
        this.competitionName = competition.getName();
        this.pendingSpendProfiles = pendingSpendProfiles;
        this.openQueryCount = openQueryCount;
        this.pendingSpendProfilesCount = pendingSpendProfilesCount;
        this.showTabs = showTabs;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public List<CompetitionPendingSpendProfilesResource> getPendingSpendProfiles() {
        return pendingSpendProfiles;
    }

    public long getOpenQueryCount() {
        return openQueryCount;
    }

    public long getPendingSpendProfilesCount() {
        return pendingSpendProfilesCount;
    }

    public boolean isShowTabs() {
        return showTabs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionPendingSpendProfilesViewModel that = (CompetitionPendingSpendProfilesViewModel) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(openQueryCount, that.openQueryCount)
                .append(pendingSpendProfilesCount, that.pendingSpendProfilesCount)
                .append(showTabs, that.showTabs)
                .append(competitionName, that.competitionName)
                .append(pendingSpendProfiles, that.pendingSpendProfiles)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(competitionName)
                .append(pendingSpendProfiles)
                .append(openQueryCount)
                .append(pendingSpendProfilesCount)
                .append(showTabs)
                .toHashCode();
    }
}

