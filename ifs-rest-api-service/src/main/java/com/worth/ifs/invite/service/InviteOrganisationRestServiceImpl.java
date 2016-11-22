package com.worth.ifs.invite.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
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