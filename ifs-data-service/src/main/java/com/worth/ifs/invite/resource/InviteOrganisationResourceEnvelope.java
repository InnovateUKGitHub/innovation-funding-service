package com.worth.ifs.invite.resource;

import com.worth.ifs.commons.resource.ResourceEnvelope;

/*
* InviteOrganisationResourceEnvelope is a explicit class definition of the ResourceEnvelope for the InviteOrganisationResource.
* It is needed in the web-service side to be able to interpret data-service JSON responses.
* */

public class InviteOrganisationResourceEnvelope extends ResourceEnvelope<InviteOrganisationResource> {
    public InviteOrganisationResourceEnvelope() {}
    public InviteOrganisationResourceEnvelope(ResourceEnvelope<InviteOrganisationResource> inviteOrganisationResourceResourceEnvelope) {
        super(inviteOrganisationResourceResourceEnvelope);
    }
}
