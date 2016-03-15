package com.worth.ifs.invite.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.InviteOrganisationResource;

public interface InviteOrganisationRestService {

    RestResult<InviteOrganisationResource> findOne(Long id);

    RestResult<Void> put(InviteOrganisationResource inviteOrganisation);
}