package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;

public interface InviteOrganisationRestService {

    RestResult<InviteOrganisationResource> getById(long id);

    RestResult<InviteOrganisationResource> getByIdForAnonymousUserFlow(long id);

    RestResult<InviteOrganisationResource> getByOrganisationIdWithInvitesForApplication(long organisationId, long applicationId);

    RestResult<Void> put(InviteOrganisationResource inviteOrganisation);
}
