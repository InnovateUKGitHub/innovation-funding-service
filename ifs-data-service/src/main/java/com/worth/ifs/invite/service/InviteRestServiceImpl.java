package com.worth.ifs.invite.service;

import com.worth.ifs.commons.resource.ResourceEnvelope;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.resource.InviteResultsResource;
import com.worth.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/*
* A typical RestService to use as a client API on the web-service side for the data-service functionality .
* */

@Service
public class InviteRestServiceImpl extends BaseRestService implements InviteRestService {
    private String inviteRestUrl;
    @Autowired
    OrganisationRestService organisationRestService;

    @Value("${ifs.data.service.rest.invite}")
    void setInviteRestUrl(String inviteRestUrl) {
        this.inviteRestUrl = inviteRestUrl;
    }

    @Override
    public ResourceEnvelope<InviteResultsResource> createInvitesByInviteOrganisation(String organisationName, List<InviteResource> invites) {
        InviteOrganisationResource inviteOrganisation = new InviteOrganisationResource();

        inviteOrganisation.setOrganisationName(organisationName);
        inviteOrganisation.setInviteResources(invites);

        String url = inviteRestUrl + "/createApplicationInvites";

        return restPost(url, inviteOrganisation, ResourceEnvelope.class);
    }

    @Override
    public ResourceEnvelope<InviteResultsResource> createInvitesByOrganisation(Long organisationId, List<InviteResource> invites) {
        InviteOrganisationResource inviteOrganisation = new InviteOrganisationResource();

        inviteOrganisation.setOrganisationId(organisationId);
        inviteOrganisation.setInviteResources(invites);

        String url = inviteRestUrl + "/createApplicationInvites";

        return restPost(url, inviteOrganisation, ResourceEnvelope.class);
    }

    @Override
    public ResourceEnvelope<InviteResultsResource> saveInvites(List<InviteResource> inviteResources) {
        String url = inviteRestUrl + "/saveInvites";
        return restPost(url, inviteResources, ResourceEnvelope.class);
    }

    @Override
    public List<InviteOrganisationResource> getInvitesByApplication(Long applicationId) {
        String url = inviteRestUrl + "/getInvitesByApplicationId/"+ applicationId;
        return Arrays.asList(restGet(url, InviteOrganisationResource[].class));
    }

}
