package org.innovateuk.ifs.project.status.viewmodel;

import org.innovateuk.ifs.project.status.security.StatusPermission;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;

import java.util.Map;

/**
 * Interface that defines the minimal information necessary to drive a standard Project page with the standard header information about the project
 */
public class CompetitionStatusViewModel {

    private CompetitionProjectsStatusResource competitionProjectsStatusResource;
    private Map<Long, StatusPermission> statusPermissions;
    private boolean canExportBankDetails;

    public CompetitionStatusViewModel(CompetitionProjectsStatusResource competitionProjectsStatusResource, boolean canExportBankDetails, Map<Long, StatusPermission> projectStatusPermissionsMap) {
        this.competitionProjectsStatusResource = competitionProjectsStatusResource;
        this.canExportBankDetails = canExportBankDetails;
        this.statusPermissions = projectStatusPermissionsMap;
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
}
