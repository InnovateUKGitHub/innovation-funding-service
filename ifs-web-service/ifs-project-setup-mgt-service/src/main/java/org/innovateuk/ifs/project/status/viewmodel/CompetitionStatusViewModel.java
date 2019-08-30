package org.innovateuk.ifs.project.status.viewmodel;

import org.innovateuk.ifs.internal.InternalProjectSetupRow;
import org.innovateuk.ifs.project.internal.ProjectSetupStages;

import java.util.List;
import java.util.Set;

/**
 * Interface that defines the minimal information necessary to drive a standard Project page with the standard header information about the project
 */
public class CompetitionStatusViewModel {

    private long competitionId;
    private String competitionName;
    private boolean showTabs;
    private boolean canExportBankDetails;
    private long openQueryCount;
    private long pendingSpendProfilesCount;
    private String applicationSearchString;
    private Set<ProjectSetupStages> columns;
    private List<InternalProjectSetupRow> rows;

    public CompetitionStatusViewModel(long competitionId,
                                      String competitionName,
                                      boolean hasProjectFinanceRole,
                                      long openQueryCount,
                                      long pendingSpendProfilesCount,
                                      String applicationSearchString,
                                      List<InternalProjectSetupRow> rows) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.showTabs = hasProjectFinanceRole;
        this.canExportBankDetails = hasProjectFinanceRole;
        this.openQueryCount = openQueryCount;
        this.pendingSpendProfilesCount = pendingSpendProfilesCount;
        this.applicationSearchString = applicationSearchString;
        this.columns = getOrderedProjectSetupColumns(rows);
        this.rows = rows;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public String getEmptyTableText() {
        return "There are currently no projects in this competition.";
    }

    public long getOpenQueryCount() { return openQueryCount; }

    public long getPendingSpendProfilesCount() { return pendingSpendProfilesCount; }

    public boolean isShowTabs() { return showTabs; }

    public String getApplicationSearchString() {
        return applicationSearchString;
    }

    public Set<ProjectSetupStages> getColumns() {
        return columns;
    }

    public List<InternalProjectSetupRow> getRows() {
        return rows;
    }

    private Set<ProjectSetupStages> getOrderedProjectSetupColumns(List<InternalProjectSetupRow> internalProjectSetupRows) {
        return internalProjectSetupRows.get(0).getStates().keySet();
    }

    public boolean isCanExportBankDetails() {
        return canExportBankDetails;
    }
}
