package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secure service for Project processing work
 */
public interface ProjectStateService {

    @PreAuthorize("hasAuthority('ifs_administrator')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectResource.class, description = "Only the IFS administrator users are able to withdraw projects")
    ServiceResult<Void> withdrawProject(long projectId);

    @PreAuthorize("hasAuthority('ifs_administrator')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectResource.class, description = "Only the IFS administrator users are able to handle projects offline")
    ServiceResult<Void> handleProjectOffline(long projectId);

    @PreAuthorize("hasAuthority('ifs_administrator')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectResource.class, description = "Only the IFS administrator users are able to complete projects offline")
    ServiceResult<Void> completeProjectOffline(long projectId);
}
