package com.worth.ifs.invite.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.security.NotSecured;

public interface InviteOrganisationService {

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<InviteOrganisationResource> findOne(Long id);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Iterable<InviteOrganisationResource>> findAll();

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<InviteOrganisationResource> save(InviteOrganisationResource inviteOrganisationResource);
}