package com.worth.ifs.invite.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.security.NotSecured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;

public interface InviteOrganisationService {

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<InviteOrganisationResource> findOne(Long id);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<Iterable<InviteOrganisationResource>> findAll();

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<InviteOrganisationResource> save(InviteOrganisationResource inviteOrganisationResource);
}