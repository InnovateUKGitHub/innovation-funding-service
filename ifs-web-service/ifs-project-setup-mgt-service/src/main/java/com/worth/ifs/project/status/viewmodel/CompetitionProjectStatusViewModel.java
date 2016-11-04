package com.worth.ifs.project.status.viewmodel;

import com.worth.ifs.project.status.controller.ProjectStatusPermission;
import com.worth.ifs.project.status.resource.CompetitionProjectsStatusResource;

import java.util.Map;

/**
 * Interface that defines the minimal information necessary to drive a standard Project page with the standard header information about the project
 */
public class CompetitionProjectStatusViewModel {

    private CompetitionProjectsStatusResource competitionProjectsStatusResource;
    private Map<Long, ProjectStatusPermission> projectStatusPermissions;
    private boolean canExportBankDetails;

    public CompetitionProjectStatusViewModel(CompetitionProjectsStatusResource competitionProjectsStatusResource, boolean canExportBankDetails, Map<Long, ProjectStatusPermission> projectStatusPermissionsMap) {
        this.competitionProjectsStatusResource = competitionProjectsStatusResource;
        this.canExportBankDetails = canExportBankDetails;
        this.projectStatusPermissions = projectStatusPermissionsMap;
    }

    public CompetitionProjectsStatusResource getCompetitionProjectsStatusResource() {
        return competitionProjectsStatusResource;
    }

    public Map<Long, ProjectStatusPermission> getProjectStatusPermissions() {
        return projectStatusPermissions;
    }

    public boolean isCanExportBankDetails() {
        return canExportBankDetails;
    }
}
