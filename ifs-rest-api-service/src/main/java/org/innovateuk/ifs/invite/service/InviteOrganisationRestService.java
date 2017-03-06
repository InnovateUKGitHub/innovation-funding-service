package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;

public interface InviteOrganisationRestService {

    RestResult<InviteOrganisationResource> getById(Long id);

    RestResult<InviteOrganisationResource> getByIdForAnonymousUserFlow(Long id);

    RestResult<Void> put(InviteOrganisationResource inviteOrganisation);
}
