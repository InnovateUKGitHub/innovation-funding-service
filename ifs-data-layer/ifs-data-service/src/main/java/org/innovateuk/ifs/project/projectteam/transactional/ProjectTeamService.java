package org.innovateuk.ifs.project.projectteam.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.ProjectUserCompositeId;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secure service for Project team processing work
 */
public interface ProjectTeamService {


    @PreAuthorize("hasPermission(#composite, 'REMOVE_PROJECT_USER')")
    ServiceResult<Void> removeUser(ProjectUserCompositeId composite);
}
