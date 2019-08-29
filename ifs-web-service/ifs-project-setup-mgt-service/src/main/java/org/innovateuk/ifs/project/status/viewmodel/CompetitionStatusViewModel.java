package org.innovateuk.ifs.project.status.viewmodel;

import org.innovateuk.ifs.project.internal.ProjectSetupColumn;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.security.StatusPermission;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private Set<ProjectSetupColumn> columns;
    private List<InternalProjectSetupRow> rows;

    public CompetitionStatusViewModel(CompetitionProjectsStatusResource competitionProjectsStatusResource,
                                      boolean hasProjectFinanceRole,
                                      Map<Long, StatusPermission> projectStatusPermissionsMap,
                                      long openQueryCount,
                                      long pendingSpendProfilesCount,
                                      String applicationSearchString,
                                      List<InternalProjectSetupRow> rows) {
        this.competitionProjectsStatusResource = competitionProjectsStatusResource;
        this.canExportBankDetails = hasProjectFinanceRole;
        this.statusPermissions = projectStatusPermissionsMap;
        this.openQueryCount = openQueryCount;
        this.pendingSpendProfilesCount = pendingSpendProfilesCount;
        this.showTabs = hasProjectFinanceRole;
        this.applicationSearchString = applicationSearchString;
        this.columns = getOrderedProjectSetupColumns(rows);
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

    public Set<ProjectSetupColumn> getColumns() {
        return columns;
    }

    public List<InternalProjectSetupRow> getRows() {
        return rows;
    }

    private Set<ProjectSetupColumn> getOrderedProjectSetupColumns(List<InternalProjectSetupRow> internalProjectSetupRows) {
        return internalProjectSetupRows.get(0).getStates().keySet();
    }
}
