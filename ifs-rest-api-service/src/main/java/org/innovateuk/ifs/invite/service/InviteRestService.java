package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

public interface InviteRestService {
    RestResult<Void> createInvitesByInviteOrganisation(String organisationName, List<ApplicationInviteResource> invites);
    RestResult<Void> createInvitesByOrganisation(Long organisationId, List<ApplicationInviteResource> invites);
    RestResult<Void> createInvitesByOrganisationForApplication(Long applicationId, Long organisationId, List<ApplicationInviteResource> invites);
    RestResult<Void> saveInvites(List<ApplicationInviteResource> inviteResources);
    RestResult<Void> acceptInvite(String inviteHash, long userId);
    RestResult<Void> acceptInvite(String inviteHash, long userId, long organisationId);
    RestResult<Void> removeApplicationInvite(Long inviteId);

    RestResult<Boolean> checkExistingUser(String inviteHash);
    RestResult<UserResource> getUser(String inviteHash);

    RestResult<ApplicationInviteResource> getInviteByHash(String hash);
    RestResult<InviteOrganisationResource> getInviteOrganisationByHash(String hash);
    RestResult<List<InviteOrganisationResource>> getInvitesByApplication(Long applicationId);
}
