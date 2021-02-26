package org.innovateuk.ifs.project.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.project.invite.resource.SendProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.resource.SentProjectPartnerInviteResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.sentProjectPartnerInviteResourceListType;

@Service
public class ProjectPartnerInviteRestServiceImpl extends BaseRestService implements ProjectPartnerInviteRestService {

    private static final String BASE_URL = "/project/%d/project-partner-invite";
    private static final String INVITE_ID_URL = BASE_URL + "/%s";

    @Override
    public RestResult<Void> invitePartnerOrganisation(long projectId, SendProjectPartnerInviteResource invite) {
        return postWithRestResult(format(BASE_URL, projectId), invite, Void.class);
    }

    @Override
    public RestResult<List<SentProjectPartnerInviteResource>> getPartnerInvites(long projectId) {
        return getWithRestResult(format(BASE_URL, projectId), sentProjectPartnerInviteResourceListType());
    }

    @Override
    public RestResult<Void> resendInvite(long projectId, long inviteId) {
        return postWithRestResult(format(INVITE_ID_URL, projectId, inviteId) + "/resend", Void.class);
    }

    @Override
    public RestResult<Void> deleteInvite(long projectId, long inviteId) {
        return deleteWithRestResult(format(INVITE_ID_URL, projectId, inviteId), Void.class);
    }

    @Override
    public RestResult<SentProjectPartnerInviteResource> getInviteByHash(long projectId, String hash) {
        return getWithRestResultAnonymous(format(INVITE_ID_URL, projectId, hash), SentProjectPartnerInviteResource.class);
    }

    @Override
    public RestResult<Void> acceptInvite(long projectId, long inviteId, long organisationId) {
        return postWithRestResultAnonymous(format(INVITE_ID_URL + "/organisation/%d/accept", projectId, inviteId, organisationId), Void.class);
    }
}
