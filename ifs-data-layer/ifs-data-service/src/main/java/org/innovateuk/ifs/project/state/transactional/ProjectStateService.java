package org.innovateuk.ifs.project.state.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secure service for Project state processing work
 */
public interface ProjectStateService {

    @PreAuthorize("hasAuthority('ifs_administrator')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectResource.class, description = "Only the IFS administrator users are able to withdraw projects")
    ServiceResult<Void> withdrawProject(long projectId);

    @PreAuthorize("hasAuthority('ifs_administrator')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectResource.class, description = "Only the IFS administrator users are able to mark projects as handled offline")
    ServiceResult<Void> handleProjectOffline(long projectId);

    @PreAuthorize("hasAuthority('ifs_administrator')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectResource.class, description = "Only the IFS administrator users are able to complete projects offline")
    ServiceResult<Void> completeProjectOffline(long projectId);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectResource.class, description = "Only the project finance users are able to put projects on hold")
    ServiceResult<Void> putProjectOnHold(long projectId);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectResource.class, description = "Only the project finance users are able to resume projects")
    ServiceResult<Void> resumeProject(long projectId);
}
