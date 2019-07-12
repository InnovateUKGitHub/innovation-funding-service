package org.innovateuk.ifs.project.status.viewmodel;

import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.security.StatusPermission;

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

    public CompetitionStatusViewModel(CompetitionProjectsStatusResource competitionProjectsStatusResource,
                                      boolean hasProjectFinanceRole,
                                      Map<Long, StatusPermission> projectStatusPermissionsMap,
                                      long openQueryCount,
                                      long pendingSpendProfilesCount) {
        this.competitionProjectsStatusResource = competitionProjectsStatusResource;
        this.canExportBankDetails = hasProjectFinanceRole;
        this.statusPermissions = projectStatusPermissionsMap;
        this.openQueryCount = openQueryCount;
        this.pendingSpendProfilesCount = pendingSpendProfilesCount;
        this.showTabs = hasProjectFinanceRole;
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
}
