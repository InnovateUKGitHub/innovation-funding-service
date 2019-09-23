package org.innovateuk.ifs.project.projectteam;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class ProjectTeamRestServiceImpl extends BaseRestService implements ProjectTeamRestService {

    private String projectTeamRestURL = "/project/%d/team/%s";

    @Override
    public RestResult<Void> inviteProjectMember(long projectId, ProjectUserInviteResource inviteResource) {
        return postWithRestResult(format(projectTeamRestURL, projectId, "invite"), inviteResource, Void.class);
    }

    @Override
    public RestResult<Void> removeUser(long projectId, long userId) {
        return postWithRestResult(format(projectTeamRestURL + "/%d", projectId, "remove-user", userId));
    }

    @Override
    public RestResult<Void> removeInvite(long projectId, long inviteId) {
        return postWithRestResult(format(projectTeamRestURL + "/%d", projectId, "remove-invite", inviteId));
    }

    @Override
    public RestResult<Void> saveProjectInvite(ProjectUserInviteResource projectUserInviteResource) {
        return postWithRestResult(format(projectTeamRestURL , "save-invite"), projectUserInviteResource, Void.class);
    }
}
