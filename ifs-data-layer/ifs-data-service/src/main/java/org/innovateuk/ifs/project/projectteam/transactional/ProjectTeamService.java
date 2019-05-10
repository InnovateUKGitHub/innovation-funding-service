package org.innovateuk.ifs.project.projectteam.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.project.resource.ProjectUserCompositeId;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secure service for Project team processing work
 */
public interface ProjectTeamService {

    @PreAuthorize("hasPermission(#composite, 'REMOVE_PROJECT_USER')")
    ServiceResult<Void> removeUser(ProjectUserCompositeId composite);

    @PreAuthorize("hasPermission(#projectUserInviteResourceId, 'org.innovateuk.ifs.invite.resource.ProjectUserInviteResource', 'DELETE_PROJECT_INVITE')")
    ServiceResult<Void> removeInvite(long projectUserInviteResourceId, long projectId);

    @PreAuthorize("hasPermission(#inviteResource, 'SEND_PROJECT_INVITE')")
    ServiceResult<Void> inviteTeamMember(long projectId, ProjectUserInviteResource inviteResource);
}