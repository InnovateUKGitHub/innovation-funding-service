package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

public interface InviteOrganisationService {

    @PostAuthorize("hasPermission(returnObject, 'READ_FOR_UPDATE')")
    ServiceResult<InviteOrganisationResource> getById(long id);

    @PostAuthorize("hasPermission(returnObject, 'READ_FOR_UPDATE')")
    ServiceResult<InviteOrganisationResource> getByOrganisationIdWithInvitesForApplication(long organisationId, long applicationId);

    @PreAuthorize(value = "hasPermission(#inviteOrganisationResource, 'SAVE')")
    ServiceResult<InviteOrganisationResource> save(@P("inviteOrganisationResource") InviteOrganisationResource inviteOrganisationResource);
}
