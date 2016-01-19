package com.worth.ifs.invite.service;

import com.worth.ifs.commons.resource.ResourceEnvelope;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;

import java.util.List;

public interface InviteRestService {
    public ResourceEnvelope<InviteOrganisationResource> createInvitesByInviteOrganisation(String organisationName, List<InviteResource> invites);
    public ResourceEnvelope<InviteOrganisationResource> createInvitesByOrganisation(Long organisationId, List<InviteResource> invites);
}
