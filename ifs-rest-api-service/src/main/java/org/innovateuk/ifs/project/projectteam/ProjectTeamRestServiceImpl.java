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
}
