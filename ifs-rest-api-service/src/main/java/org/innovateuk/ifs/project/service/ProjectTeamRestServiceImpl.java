package org.innovateuk.ifs.project.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.springframework.stereotype.Service;

@Service
public class ProjectTeamRestServiceImpl extends BaseRestService implements ProjectTeamRestService {

    private String projectRestURL = "/project/%d/team";

    @Override
    public RestResult<Void> inviteProjectMember(Long projectId, ProjectUserInviteResource inviteResource) {
        return null;
    }
}
