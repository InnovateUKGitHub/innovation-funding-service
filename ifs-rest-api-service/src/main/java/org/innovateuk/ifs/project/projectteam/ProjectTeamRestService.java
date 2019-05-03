package org.innovateuk.ifs.project.projectteam;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;

public interface ProjectTeamRestService {

    RestResult<Void> inviteProjectMember(long projectId, ProjectUserInviteResource inviteResource);
}
