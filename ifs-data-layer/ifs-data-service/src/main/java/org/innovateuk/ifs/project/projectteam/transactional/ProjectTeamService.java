package org.innovateuk.ifs.project.projectteam.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.project.resource.ProjectUserCompositeId;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

/**
 * Transactional and secure service for Project team processing work
 */
public interface ProjectTeamService {

    @PreAuthorize("hasPermission(#composite, 'REMOVE_PROJECT_USER')")
    ServiceResult<Void> removeUser(ProjectUserCompositeId composite);

    @PreAuthorize("hasPermission(#projectUserInviteResourceId, 'org.innovateuk.ifs.invite.resource.ProjectUserInviteResource', 'DELETE_PROJECT_INVITE')")
    ServiceResult<Void> removeInvite(@P("projectUserInviteResourceId") long projectUserInviteResourceId, long projectId);

    @PreAuthorize("hasPermission(#inviteResource, 'SEND_PROJECT_INVITE')")
    ServiceResult<Void> inviteTeamMember(long projectId, ProjectUserInviteResource inviteResource);

    @PreAuthorize("hasPermission(#projectUserInviteResource, 'SAVE_PROJECT_INVITE')")
    ServiceResult<Void> saveProjectInvite(@P("projectUserInviteResource") ProjectUserInviteResource projectUserInviteResource);
}