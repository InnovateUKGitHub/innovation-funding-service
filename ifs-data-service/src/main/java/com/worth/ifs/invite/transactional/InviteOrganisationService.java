package com.worth.ifs.invite.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

public interface InviteOrganisationService {

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<InviteOrganisationResource> findOne(Long id);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<Iterable<InviteOrganisationResource>> findAll();

    @PreAuthorize(value = "hasPermission(#inviteOrganisationResource, 'SAVE')")
    ServiceResult<InviteOrganisationResource> save(@P("inviteOrganisationResource")InviteOrganisationResource inviteOrganisationResource);
}