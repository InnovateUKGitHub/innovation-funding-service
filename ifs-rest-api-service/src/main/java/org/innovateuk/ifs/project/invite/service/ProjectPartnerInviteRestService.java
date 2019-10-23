package org.innovateuk.ifs.project.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.invite.resource.ProjectPartnerInviteResource;

public interface ProjectPartnerInviteRestService {

    RestResult<Void> invitePartnerOrganisation(long projectId, ProjectPartnerInviteResource invite);

}
