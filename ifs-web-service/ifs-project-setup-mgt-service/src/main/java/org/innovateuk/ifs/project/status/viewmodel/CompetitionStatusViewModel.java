package org.innovateuk.ifs.project.status.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.internal.InternalProjectSetupRow;
import org.innovateuk.ifs.pagination.PaginationViewModel;
import org.innovateuk.ifs.status.viewmodel.BaseCompetitionStatusTableViewModel;

import java.util.List;

public class CompetitionStatusViewModel extends BaseCompetitionStatusTableViewModel {

    private boolean showTabs;
    private long openQueryCount;
    private long pendingSpendProfilesCount;
    private String applicationSearchString;
    private PaginationViewModel paginationViewModel;

    public CompetitionStatusViewModel(CompetitionResource competition,
                                      boolean hasProjectFinanceRole,
                                      long openQueryCount,
                                      long pendingSpendProfilesCount,
                                      String applicationSearchString,
                                      List<InternalProjectSetupRow> rows,
                                      PaginationViewModel paginationViewModel,
                                      boolean externalFinanceUser,
                                      boolean adminUser) {
        super(competition, rows, hasProjectFinanceRole, externalFinanceUser, adminUser);
        this.showTabs = hasProjectFinanceRole;
        this.openQueryCount = openQueryCount;
        this.pendingSpendProfilesCount = pendingSpendProfilesCount;
        this.applicationSearchString = applicationSearchString;
        this.paginationViewModel = paginationViewModel;
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

    public PaginationViewModel getPaginationViewModel() {
        return paginationViewModel;
    }

}
