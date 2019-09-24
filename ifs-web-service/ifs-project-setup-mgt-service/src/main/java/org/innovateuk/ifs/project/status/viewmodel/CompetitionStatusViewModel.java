package org.innovateuk.ifs.project.status.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.internal.InternalProjectSetupRow;
import org.innovateuk.ifs.status.viewmodel.BaseCompetitionStatusTableViewModel;

import java.util.List;

public class CompetitionStatusViewModel extends BaseCompetitionStatusTableViewModel {

    private boolean showTabs;
    private long openQueryCount;
    private long pendingSpendProfilesCount;
    private String applicationSearchString;

    public CompetitionStatusViewModel(CompetitionResource competition,
                                      boolean hasProjectFinanceRole,
                                      long openQueryCount,
                                      long pendingSpendProfilesCount,
                                      String applicationSearchString,
                                      List<InternalProjectSetupRow> rows) {
        super(competition, rows, hasProjectFinanceRole);
        this.showTabs = hasProjectFinanceRole;
        this.openQueryCount = openQueryCount;
        this.pendingSpendProfilesCount = pendingSpendProfilesCount;
        this.applicationSearchString = applicationSearchString;
    }

    @Override
    public String getEmptyTableText() {
        return "There are currently no projects in this competition.";
    }

    public long getOpenQueryCount() { return openQueryCount; }

    public long getPendingSpendProfilesCount() { return pendingSpendProfilesCount; }

    public boolean isShowTabs() { return showTabs; }

    public String getApplicationSearchString() {
        return applicationSearchString;
    }

}
