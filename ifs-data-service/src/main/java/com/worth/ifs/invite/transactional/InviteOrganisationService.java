package com.worth.ifs.invite.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.security.NotSecured;

public interface InviteOrganisationService {

    @NotSecured("TODO")
    ServiceResult<InviteOrganisationResource> findOne(Long id);
}