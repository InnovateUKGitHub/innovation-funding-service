package org.innovateuk.ifs.project.state.transactional;

import org.innovateuk.ifs.activitylog.advice.Activity;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.state.OnHoldReasonResource;
import org.springframework.security.access.prepost.PreAuthorize;

import static org.innovateuk.ifs.activitylog.resource.ActivityType.*;

/**
 * Transactional and secure service for Project state processing work
 */
public interface ProjectStateService {

    @PreAuthorize("hasAuthority('ifs_administrator')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectResource.class, description = "Only the IFS administrator users are able to withdraw projects")
    @Activity(type = WITHDRAWN, projectId = "projectId")
    ServiceResult<Void> withdrawProject(long projectId);

    @PreAuthorize("hasAuthority('ifs_administrator')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectResource.class, description = "Only the IFS administrator users are able to mark projects as handled offline")
    @Activity(type = MANAGED_OFFLINE, projectId = "projectId")
    ServiceResult<Void> handleProjectOffline(long projectId);

    @PreAuthorize("hasAuthority('ifs_administrator')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectResource.class, description = "Only the IFS administrator users are able to complete projects offline")
    @Activity(type = COMPLETE_OFFLINE, projectId = "projectId")
    ServiceResult<Void> completeProjectOffline(long projectId);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectResource.class, description = "Only the project finance users are able to put projects on hold")
    @Activity(type = ON_HOLD, projectId = "projectId")
    ServiceResult<Void> putProjectOnHold(long projectId, OnHoldReasonResource reason);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectResource.class, description = "Only the project finance users are able to resume projects")
    @Activity(type = RESUMED_FROM_ON_HOLD, projectId = "projectId")
    ServiceResult<Void> resumeProject(long projectId);
}
