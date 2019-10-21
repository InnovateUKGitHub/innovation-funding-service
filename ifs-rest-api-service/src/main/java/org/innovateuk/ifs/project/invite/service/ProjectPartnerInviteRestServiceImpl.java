package org.innovateuk.ifs.project.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.project.invite.resource.ProjectPartnerInviteResource;
import org.springframework.stereotype.Service;

@Service
public class ProjectPartnerInviteRestServiceImpl extends BaseRestService implements ProjectPartnerInviteRestService {

    @Override
    public RestResult<Void> invitePartnerOrganisation(long projectId, ProjectPartnerInviteResource invite) {
        String url = String.format("/project/%d/project-partner-invite", projectId);
        return postWithRestResult(url, invite, Void.class);
    }

}
