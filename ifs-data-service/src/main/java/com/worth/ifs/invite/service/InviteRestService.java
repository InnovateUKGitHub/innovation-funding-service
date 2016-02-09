package com.worth.ifs.invite.service;

import com.worth.ifs.commons.resource.ResourceEnvelope;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.resource.InviteResultsResource;

import java.util.List;
import java.util.Optional;

public interface InviteRestService {
    ResourceEnvelope<InviteResultsResource> createInvitesByInviteOrganisation(String organisationName, List<InviteResource> invites);
    ResourceEnvelope<InviteResultsResource> createInvitesByOrganisation(Long organisationId, List<InviteResource> invites);
    ResourceEnvelope<InviteResultsResource> saveInvites(List<InviteResource> inviteResources);
    Optional<InviteResource> getInviteByHash(String hash);
    Optional<InviteOrganisationResource> getInviteOrganisationByHash(String hash);
    List<InviteOrganisationResource> getInvitesByApplication(Long applicationId);
}
