package org.innovateuk.ifs.project.invite.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.invite.resource.ProjectPartnerInviteResource;

public interface ProjectPartnerInviteService {

    ServiceResult<Void> invitePartnerOrganisation(long projectId, ProjectPartnerInviteResource invite);
}