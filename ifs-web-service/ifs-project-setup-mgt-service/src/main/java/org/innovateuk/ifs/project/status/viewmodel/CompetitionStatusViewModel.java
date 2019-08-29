package org.innovateuk.ifs.project.status.viewmodel;

import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.security.StatusPermission;

import java.util.List;
import java.util.Map;

/**
 * Interface that defines the minimal information necessary to drive a standard Project page with the standard header information about the project
 */
public class CompetitionStatusViewModel implements CompetitionStatusTableViewModel {

    private CompetitionProjectsStatusResource competitionProjectsStatusResource;
    private Map<Long, StatusPermission> statusPermissions;
    private boolean canExportBankDetails;
    private long openQueryCount;
    private long pendingSpendProfilesCount;
    private boolean showTabs;
    private String applicationSearchString;
    private boolean showBankDetailsTab;
    private List<InternalProjectSetupColumn> columns;
    private List<InternalProjectSetupRow> rows;

    public CompetitionStatusViewModel(CompetitionProjectsStatusResource competitionProjectsStatusResource,
                                      boolean hasProjectFinanceRole,
                                      Map<Long, StatusPermission> projectStatusPermissionsMap,
                                      long openQueryCount,
                                      long pendingSpendProfilesCount,
                                      String applicationSearchString,
                                      boolean showBankDetailsTab,
                                      List<InternalProjectSetupColumn> columns,
                                      List<InternalProjectSetupRow> rows) {
        this.competitionProjectsStatusResource = competitionProjectsStatusResource;
        this.canExportBankDetails = hasProjectFinanceRole;
        this.statusPermissions = projectStatusPermissionsMap;
        this.openQueryCount = openQueryCount;
        this.pendingSpendProfilesCount = pendingSpendProfilesCount;
        this.showTabs = hasProjectFinanceRole;
        this.applicationSearchString = applicationSearchString;
        this.showBankDetailsTab = showBankDetailsTab;
        this.columns = columns;
        this.rows = rows;
    }

    @Override
    public CompetitionProjectsStatusResource getCompetitionProjectsStatusResource() {
        return competitionProjectsStatusResource;
    }

    @Override
    public Map<Long, StatusPermission> getStatusPermissions() {
        return statusPermissions;
    }

    @Override
    public boolean isCanExportBankDetails() {
        return canExportBankDetails;
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

    public boolean isShowBankDetailsTab() {
        return showBankDetailsTab;
    }

    public List<InternalProjectSetupColumn> getColumns() {
        return columns;
    }

    public List<InternalProjectSetupRow> getRows() {
        return rows;
    }
}
