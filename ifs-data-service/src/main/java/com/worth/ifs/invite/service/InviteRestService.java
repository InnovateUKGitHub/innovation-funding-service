package com.worth.ifs.invite.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.resource.InviteResultsResource;

import java.util.List;

public interface InviteRestService {
    RestResult<InviteResultsResource> createInvitesByInviteOrganisation(String organisationName, List<InviteResource> invites);
    RestResult<InviteResultsResource> createInvitesByOrganisation(Long organisationId, List<InviteResource> invites);
    RestResult<InviteResultsResource> saveInvites(List<InviteResource> inviteResources);
    RestResult<Void> acceptInvite(String inviteHash, Long userId);

    RestResult<Boolean> checkExistingUser(String inviteHash);

    RestResult<InviteResource> getInviteByHash(String hash);
    RestResult<InviteOrganisationResource> getInviteOrganisationByHash(String hash);
    RestResult<List<InviteOrganisationResource>> getInvitesByApplication(Long applicationId);
}
