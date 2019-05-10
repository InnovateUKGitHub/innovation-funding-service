package org.innovateuk.ifs.project.projectteam;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.springframework.stereotype.Service;

@Service
public class ProjectTeamRestServiceImpl extends BaseRestService implements ProjectTeamRestService {

    private String projectTeamRestURL = "/project/%d/team/%s";

    @Override
    public RestResult<Void> inviteProjectMember(long projectId, ProjectUserInviteResource inviteResource) {
        return postWithRestResult(String.format(projectTeamRestURL, projectId, "invite"), inviteResource, Void.class);
    }

    @Override
    public RestResult<Void> removeUser(long projectId, long userId) {
        return postWithRestResult(String.format(projectTeamRestURL + "/%d", projectId, "remove-user", userId));
    }

    @Override
    public RestResult<Void> removeInvite(long projectId, long inviteId) {
        return postWithRestResult(String.format(projectTeamRestURL + "/%d", projectId, "remove-invite", inviteId));
    }
}
