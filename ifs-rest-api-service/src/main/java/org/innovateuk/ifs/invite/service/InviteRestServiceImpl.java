package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.inviteOrganisationResourceListType;
import static org.innovateuk.ifs.invite.resource.InviteProjectConstants.GET_USER_BY_HASH_MAPPING;

/*
* A typical RestService to use as a client API on the web-service side for the data-service functionality .
* */

@Service
public class InviteRestServiceImpl extends BaseRestService implements InviteRestService {

    private String inviteRestUrl = "/invite";

    @Override
    public RestResult<InviteResultsResource> createInvitesByInviteOrganisation(String organisationName, List<ApplicationInviteResource> invites) {
        InviteOrganisationResource inviteOrganisation = new InviteOrganisationResource();

        inviteOrganisation.setOrganisationName(organisationName);
        inviteOrganisation.setInviteResources(invites);

        String url = inviteRestUrl + "/createApplicationInvites";

        return postWithRestResult(url, inviteOrganisation, InviteResultsResource.class);
    }

    @Override
    public RestResult<InviteResultsResource> createInvitesByOrganisation(Long organisationId, List<ApplicationInviteResource> invites) {
        InviteOrganisationResource inviteOrganisation = new InviteOrganisationResource();

        inviteOrganisation.setOrganisation(organisationId);
        inviteOrganisation.setInviteResources(invites);

        String url = inviteRestUrl + "/createApplicationInvites";

        return postWithRestResult(url, inviteOrganisation, InviteResultsResource.class);
    }

    @Override
    public RestResult<InviteResultsResource> createInvitesByOrganisationForApplication(Long applicationId, Long organisationId, List<ApplicationInviteResource> invites) {
        InviteOrganisationResource inviteOrganisation = new InviteOrganisationResource();

        inviteOrganisation.setOrganisation(organisationId);
        inviteOrganisation.setInviteResources(invites);


        String url = inviteRestUrl + String.format("/createApplicationInvites/%s", applicationId);

        return postWithRestResult(url, inviteOrganisation, InviteResultsResource.class);
    }

    @Override
    public RestResult<InviteResultsResource> saveInvites(List<ApplicationInviteResource> inviteResources) {
        String url = inviteRestUrl + "/saveInvites";
        return postWithRestResult(url, inviteResources, InviteResultsResource.class);
    }

    @Override
    public RestResult<Void> acceptInvite(String inviteHash, Long userId) {
        String url = inviteRestUrl + String.format("/acceptInvite/%s/%s", inviteHash, userId);
        return putWithRestResultAnonymous(url, Void.class);
    }

    @Override
    public RestResult<Void> removeApplicationInvite(Long inviteId) {
        String url = inviteRestUrl + String.format("/removeInvite/%s", inviteId);
        return deleteWithRestResult(url);
    }

    @Override
    public RestResult<Boolean> checkExistingUser(String inviteHash) {
        String url = inviteRestUrl + String.format("/checkExistingUser/%s", inviteHash);
        return getWithRestResultAnonymous(url, Boolean.class);
    }

    @Override
    public RestResult<UserResource> getUser(String inviteHash) {
        String url = inviteRestUrl + String.format(GET_USER_BY_HASH_MAPPING + "%s", inviteHash);
        return getWithRestResultAnonymous(url, UserResource.class);
    }

    @Override
    public RestResult<ApplicationInviteResource> getInviteByHash(String hash) {
        return getWithRestResultAnonymous(inviteRestUrl + "/getInviteByHash/" + hash, ApplicationInviteResource.class);
    }

    @Override
    public RestResult<InviteOrganisationResource> getInviteOrganisationByHash(String hash) {
        return getWithRestResultAnonymous(inviteRestUrl + "/getInviteOrganisationByHash/" + hash, InviteOrganisationResource.class);
    }

    @Override
    public RestResult<List<InviteOrganisationResource>> getInvitesByApplication(Long applicationId) {
        String url = inviteRestUrl + "/getInvitesByApplicationId/"+ applicationId;
        return getWithRestResult(url, inviteOrganisationResourceListType());
    }

}
