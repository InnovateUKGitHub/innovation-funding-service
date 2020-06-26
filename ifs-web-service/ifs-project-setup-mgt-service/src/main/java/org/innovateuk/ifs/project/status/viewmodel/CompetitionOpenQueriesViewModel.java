package org.innovateuk.ifs.project.status.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.util.List;

public class CompetitionOpenQueriesViewModel {
    private long competitionId;
    private String competitionName;
    private List<CompetitionOpenQueryResource> openQueries;
    private long openQueryCount;
    private long pendingSpendProfilesCount;
    private boolean showTabs;
    private boolean externalFinanceUser;
    private boolean ifsAdmin;

    public CompetitionOpenQueriesViewModel(CompetitionResource competition, List<CompetitionOpenQueryResource> openQueries,
                                           long openQueryCount, long pendingSpendProfilesCount, boolean showTabs, boolean externalFinanceUser, boolean ifsAdmin) {
        this.competitionId = competition.getId();
        this.competitionName = competition.getName();
        this.openQueries = openQueries;
        this.openQueryCount = openQueryCount;
        this.pendingSpendProfilesCount = pendingSpendProfilesCount;
        this.showTabs = showTabs;
        this.externalFinanceUser = externalFinanceUser;
        this.ifsAdmin = ifsAdmin;
    }

    public long getCompetitionId() { return competitionId; }

    public String getCompetitionName() { return competitionName; }

    public List<CompetitionOpenQueryResource> getOpenQueries() { return openQueries; }

    public long getOpenQueryCount() { return openQueryCount; }

    public long getPendingSpendProfilesCount() { return pendingSpendProfilesCount; }

    public boolean isShowTabs() { return showTabs; }

    public boolean isExternalFinanceUser() {
        return externalFinanceUser;
    }

    public boolean isIfsAdmin() {
        return ifsAdmin;
    }
}
