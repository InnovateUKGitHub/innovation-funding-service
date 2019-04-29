package org.innovateuk.ifs.project.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;

public interface ProjectTeamRestService {

    RestResult<Void> inviteProjectMember(Long projectId, ProjectUserInviteResource inviteResource);

}
