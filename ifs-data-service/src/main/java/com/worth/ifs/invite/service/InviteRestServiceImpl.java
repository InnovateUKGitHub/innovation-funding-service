package com.worth.ifs.invite.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.resource.InviteResultsResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.inviteOrganisationResourceListType;

/*
* A typical RestService to use as a client API on the web-service side for the data-service functionality .
* */

@Service
public class InviteRestServiceImpl extends BaseRestService implements InviteRestService {

    private String inviteRestUrl;

    @Value("${ifs.data.service.rest.invite}")
    void setInviteRestUrl(String inviteRestUrl) {
        this.inviteRestUrl = inviteRestUrl;
    }

    @Override
    public RestResult<InviteResultsResource> createInvitesByInviteOrganisation(String organisationName, List<InviteResource> invites) {
        InviteOrganisationResource inviteOrganisation = new InviteOrganisationResource();

        inviteOrganisation.setOrganisationName(organisationName);
        inviteOrganisation.setInviteResources(invites);

        String url = inviteRestUrl + "/createApplicationInvites";

        return postWithRestResult(url, inviteOrganisation, InviteResultsResource.class);
    }

    @Override
    public RestResult<InviteResultsResource> createInvitesByOrganisation(Long organisationId, List<InviteResource> invites) {
        InviteOrganisationResource inviteOrganisation = new InviteOrganisationResource();

        inviteOrganisation.setOrganisation(organisationId);
        inviteOrganisation.setInviteResources(invites);

        String url = inviteRestUrl + "/createApplicationInvites";

        return postWithRestResult(url, inviteOrganisation, InviteResultsResource.class);
    }

    @Override
    public RestResult<InviteResultsResource> saveInvites(List<InviteResource> inviteResources) {
        String url = inviteRestUrl + "/saveInvites";
        return postWithRestResult(url, inviteResources, InviteResultsResource.class);
    }

    @Override
    public RestResult<InviteResource> getInviteByHash(String hash) {
        return getWithRestResult(inviteRestUrl + "/getInviteByHash/" + hash, InviteResource.class);
    }

    @Override
    public RestResult<InviteOrganisationResource> getInviteOrganisationByHash(String hash) {
        return getWithRestResult(inviteRestUrl + "/getInviteOrganisationByHash/"+hash, InviteOrganisationResource.class);
    }


    @Override
    public RestResult<List<InviteOrganisationResource>> getInvitesByApplication(Long applicationId) {
        String url = inviteRestUrl + "/getInvitesByApplicationId/"+ applicationId;
        return getWithRestResult(url, inviteOrganisationResourceListType());
    }

}
