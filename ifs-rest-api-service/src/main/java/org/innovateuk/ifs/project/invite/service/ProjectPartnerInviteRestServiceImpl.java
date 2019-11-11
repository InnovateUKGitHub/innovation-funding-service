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

    private static final String baseUrl = "/project/%d/project-partner-invite";
    private static final String inviteIdUrl = baseUrl + "/%s";

    @Override
    public RestResult<Void> invitePartnerOrganisation(long projectId, SendProjectPartnerInviteResource invite) {
        return postWithRestResult(format(baseUrl, projectId), invite, Void.class);
    }

    @Override
    public RestResult<List<SentProjectPartnerInviteResource>> getPartnerInvites(long projectId) {
        return getWithRestResult(format(baseUrl, projectId), sentProjectPartnerInviteResourceListType());
    }

    @Override
    public RestResult<Void> resendInvite(long projectId, long inviteId) {
        return postWithRestResult(format(inviteIdUrl, projectId, inviteId) + "/resend", Void.class);
    }

    @Override
    public RestResult<Void> deleteInvite(long projectId, long inviteId) {
        return deleteWithRestResult(format(inviteIdUrl, projectId, inviteId), Void.class);
    }

    @Override
    public RestResult<SentProjectPartnerInviteResource> getInviteByHash(long projectId, String hash) {
        return getWithRestResultAnonymous(format(inviteIdUrl, projectId, hash), SentProjectPartnerInviteResource.class);
    }

    @Override
    public RestResult<Void> acceptInvite(long projectId, long inviteId, long organisationId) {
        return postWithRestResultAnonymous(format(inviteIdUrl + "/organisation/%d/accept", projectId, inviteId, organisationId), Void.class);
    }
}
