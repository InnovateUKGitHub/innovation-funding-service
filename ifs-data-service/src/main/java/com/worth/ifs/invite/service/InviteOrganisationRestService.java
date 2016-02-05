package com.worth.ifs.invite.service;

import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.security.NotSecured;

public interface InviteOrganisationRestService {
    @NotSecured("REST Service")
    InviteOrganisationResource findOne(Long id);
}