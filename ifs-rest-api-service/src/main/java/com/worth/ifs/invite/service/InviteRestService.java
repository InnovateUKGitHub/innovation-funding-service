package com.worth.ifs.invite.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResultsResource;
import com.worth.ifs.user.resource.UserResource;

import java.util.List;

public interface InviteRestService {
    RestResult<InviteResultsResource> createInvitesByInviteOrganisation(String organisationName, List<ApplicationInviteResource> invites);
    RestResult<InviteResultsResource> createInvitesByOrganisation(Long organisationId, List<ApplicationInviteResource> invites);
    RestResult<InviteResultsResource> saveInvites(List<ApplicationInviteResource> inviteResources);
    RestResult<Void> acceptInvite(String inviteHash, Long userId);
    RestResult<Void> removeApplicationInvite(Long inviteId);

    RestResult<Boolean> checkExistingUser(String inviteHash);
    RestResult<UserResource> getUser(String inviteHash);

    RestResult<ApplicationInviteResource> getInviteByHash(String hash);
    RestResult<InviteOrganisationResource> getInviteOrganisationByHash(String hash);
    RestResult<List<InviteOrganisationResource>> getInvitesByApplication(Long applicationId);
}
