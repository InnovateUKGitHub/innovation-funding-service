package org.innovateuk.ifs.project.status.viewmodel;

import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.security.StatusPermission;

import java.util.Map;


/**
 * View model for the fragment fragments/status :: status-table
 */
public interface CompetitionStatusTableViewModel {

    CompetitionProjectsStatusResource getCompetitionProjectsStatusResource();
    Map<Long, StatusPermission> getStatusPermissions();

}
