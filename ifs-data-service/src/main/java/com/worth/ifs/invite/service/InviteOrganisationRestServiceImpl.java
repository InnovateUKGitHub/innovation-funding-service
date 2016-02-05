package com.worth.ifs.invite.service;

import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InviteOrganisationRestServiceImpl extends BaseRestService implements InviteOrganisationRestService {
    @Value("${ifs.data.service.rest.inviteorganisation}")
    private String restUrl;


    @Override
    public InviteOrganisationResource findOne(Long id) {
        return restGet(restUrl + "/" + id, InviteOrganisationResource.class);
    }
}