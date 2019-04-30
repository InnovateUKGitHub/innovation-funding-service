package org.innovateuk.ifs.project.projectteam.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ProjectTeamService {

    @PreAuthorize("hasPermission(#inviteResource, 'SEND_PROJECT_INVITE')")
    ServiceResult<Void> inviteTeamMember(Long projectId, ProjectUserInviteResource inviteResource);
}
