package org.innovateuk.ifs.project.status.viewmodel;

import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.security.StatusPermission;

import java.util.Map;

/**
 * Interface that defines the minimal information necessary to drive a standard Project page with the standard header information about the project
 */
public class CompetitionStatusViewModel {

    private CompetitionProjectsStatusResource competitionProjectsStatusResource;
    private Map<Long, StatusPermission> statusPermissions;
    private boolean canExportBankDetails;
    private long openQueryCount;
    private long pendingSpendProfilesCount;
    private boolean showTabs;
    private String applicationSearchString;

    public CompetitionStatusViewModel(CompetitionProjectsStatusResource competitionProjectsStatusResource,
                                      boolean hasProjectFinanceRole,
                                      Map<Long, StatusPermission> projectStatusPermissionsMap,
                                      long openQueryCount,
                                      long pendingSpendProfilesCount,
                                      String applicationSearchString) {
        this.competitionProjectsStatusResource = competitionProjectsStatusResource;
        this.canExportBankDetails = hasProjectFinanceRole;
        this.statusPermissions = projectStatusPermissionsMap;
        this.openQueryCount = openQueryCount;
        this.pendingSpendProfilesCount = pendingSpendProfilesCount;
        this.showTabs = hasProjectFinanceRole;
        this.applicationSearchString = applicationSearchString;
    }

    public CompetitionProjectsStatusResource getCompetitionProjectsStatusResource() {
        return competitionProjectsStatusResource;
    }

    public Map<Long, StatusPermission> getStatusPermissions() {
        return statusPermissions;
    }

    public boolean isCanExportBankDetails() {
        return canExportBankDetails;
    }

    public long getOpenQueryCount() { return openQueryCount; }

    public long getPendingSpendProfilesCount() { return pendingSpendProfilesCount; }

    public boolean isShowTabs() { return showTabs; }

    public String getApplicationSearchString() {
        return applicationSearchString;
    }
}
