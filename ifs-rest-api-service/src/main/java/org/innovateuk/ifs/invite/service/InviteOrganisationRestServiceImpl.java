package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.springframework.stereotype.Service;

@Service
public class InviteOrganisationRestServiceImpl extends BaseRestService implements InviteOrganisationRestService {

    private String restUrl = "/inviteorganisation";

    @Override
    public RestResult<InviteOrganisationResource> findOne(Long id) {
        return getWithRestResult(restUrl + "/" + id, InviteOrganisationResource.class);
    }

    @Override
    public RestResult<Void> put(InviteOrganisationResource inviteOrganisation) {
        return putWithRestResult(restUrl+ "/save", inviteOrganisation, Void.class);
    }
}
