package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

public interface InviteOrganisationService {

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<InviteOrganisationResource> findOne(Long id);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<InviteOrganisationResource> getByIdWithInvitesForApplication(long id, long applicationId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<InviteOrganisationResource> getByOrganisationIdWithInvitesForApplication(long organisationId, long applicationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<Iterable<InviteOrganisationResource>> findAll();

    @PreAuthorize(value = "hasPermission(#inviteOrganisationResource, 'SAVE')")
    ServiceResult<InviteOrganisationResource> save(@P("inviteOrganisationResource") InviteOrganisationResource inviteOrganisationResource);
}
