package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class InviteOrganisationRestServiceImpl extends BaseRestService implements InviteOrganisationRestService {

    private String restUrl = "/inviteorganisation";

    @Override
    public RestResult<InviteOrganisationResource> getById(long id) {
        return getWithRestResult(format("%s/%s", restUrl, id), InviteOrganisationResource.class);
    }

    @Override
    public RestResult<InviteOrganisationResource> getByIdForAnonymousUserFlow(long id) {
        return getWithRestResultAnonymous(format("%s/%s", restUrl, id), InviteOrganisationResource.class);
    }

    @Override
    public RestResult<InviteOrganisationResource> getByOrganisationIdWithInvitesForApplication(long organisationId, long applicationId) {
        return getWithRestResult(format("%s/organisation/%s/application/%s", restUrl, organisationId, applicationId), InviteOrganisationResource.class);
    }

    @Override
    public RestResult<Void> put(InviteOrganisationResource inviteOrganisation) {
        return putWithRestResult(restUrl + "/save", inviteOrganisation, Void.class);
    }
}
