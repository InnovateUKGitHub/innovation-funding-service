package org.innovateuk.ifs.project.status;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import java.util.Optional;

/**
 * A service for dealing with Status section (Setup Status, Team Status and Competition Status) via the appropriate Rest services
 */
public interface StatusService {

    @NotSecured("Not currently secured")
    ProjectTeamStatusResource getProjectTeamStatus(Long projectId, Optional<Long> filterByUserId);

    @NotSecured("Not currently secured")
    ProjectStatusResource getProjectStatus(Long projectId);
}
