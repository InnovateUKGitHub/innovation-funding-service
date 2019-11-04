package org.innovateuk.ifs.project.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.invite.resource.SendProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.resource.SentProjectPartnerInviteResource;

import java.util.List;

public interface ProjectPartnerInviteRestService {

    RestResult<Void> invitePartnerOrganisation(long projectId, SendProjectPartnerInviteResource invite);

    RestResult<List<SentProjectPartnerInviteResource>> getPartnerInvites(long projectId);

    RestResult<Void> resendInvite(long projectId, long inviteId);

    RestResult<Void> deleteInvite(long projectId, long inviteId);
}
