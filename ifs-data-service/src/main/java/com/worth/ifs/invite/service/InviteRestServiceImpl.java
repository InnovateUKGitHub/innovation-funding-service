package com.worth.ifs.invite.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.resource.InviteResultsResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    public InviteResultsResource createInvitesByInviteOrganisation(String organisationName, List<InviteResource> invites) {
        InviteOrganisationResource inviteOrganisation = new InviteOrganisationResource();

        inviteOrganisation.setOrganisationName(organisationName);
        inviteOrganisation.setInviteResources(invites);

        String url = inviteRestUrl + "/createApplicationInvites";

        return restPost(url, inviteOrganisation, InviteResultsResource.class);
    }

    @Override
    public InviteResultsResource createInvitesByOrganisation(Long organisationId, List<InviteResource> invites) {
        InviteOrganisationResource inviteOrganisation = new InviteOrganisationResource();

        inviteOrganisation.setOrganisation(organisationId);
        inviteOrganisation.setInviteResources(invites);

        String url = inviteRestUrl + "/createApplicationInvites";

        return restPost(url, inviteOrganisation, InviteResultsResource.class);
    }

    @Override
    public InviteResultsResource saveInvites(List<InviteResource> inviteResources) {
        String url = inviteRestUrl + "/saveInvites";
        return restPost(url, inviteResources, InviteResultsResource.class);
    }

    @Override
    public Optional<InviteResource> getInviteByHash(String hash) {
        RestResult<InviteResource> resource = getWithRestResult(inviteRestUrl + "/getInviteByHash/" + hash, InviteResource.class);

        if (resource.isSuccess()) {
            return Optional.ofNullable(resource.getSuccessObject());
        }
        return Optional.empty();
    }

    @Override
    public Optional<InviteOrganisationResource> getInviteOrganisationByHash(String hash) {
        RestResult<InviteOrganisationResource> resource = getWithRestResult(inviteRestUrl + "/getInviteOrganisationByHash/"+hash, InviteOrganisationResource.class);

        if (resource.isSuccess()) {
            return Optional.ofNullable(resource.getSuccessObject());
        }
        return Optional.empty();
    }


    @Override
    public List<InviteOrganisationResource> getInvitesByApplication(Long applicationId) {
        String url = inviteRestUrl + "/getInvitesByApplicationId/"+ applicationId;
        return Arrays.asList(restGet(url, InviteOrganisationResource[].class));
    }

}
