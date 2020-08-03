package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.inviteOrganisationResourceListType;
import static org.innovateuk.ifs.invite.resource.ProjectInviteConstants.GET_USER_BY_HASH_MAPPING;

/*
* A typical RestService to use as a client API on the web-service side for the data-service functionality .
* */

@Service
public class InviteRestServiceImpl extends BaseRestService implements InviteRestService {

    private String inviteRestUrl = "/invite";

    @Override
    public RestResult<Void> createInvitesByInviteOrganisation(String organisationName, List<ApplicationInviteResource> invites) {
        InviteOrganisationResource inviteOrganisation = new InviteOrganisationResource();

        inviteOrganisation.setOrganisationName(organisationName);
        inviteOrganisation.setInviteResources(invites);

        String url = inviteRestUrl + "/create-application-invites";

        return postWithRestResult(url, inviteOrganisation, Void.class);
    }

    @Override
    public RestResult<Void> createInvitesByOrganisation(Long organisationId, List<ApplicationInviteResource> invites) {
        InviteOrganisationResource inviteOrganisation = new InviteOrganisationResource();

        inviteOrganisation.setOrganisation(organisationId);
        inviteOrganisation.setInviteResources(invites);

        String url = inviteRestUrl + "/create-application-invites";

        return postWithRestResult(url, inviteOrganisation, Void.class);
    }

    @Override
    public RestResult<Void> createInvitesByOrganisationForApplication(Long applicationId, Long organisationId, List<ApplicationInviteResource> invites) {
        InviteOrganisationResource inviteOrganisation = new InviteOrganisationResource();

        inviteOrganisation.setOrganisation(organisationId);
        inviteOrganisation.setInviteResources(invites);


        String url = inviteRestUrl + String.format("/create-application-invites/%s", applicationId);

        return postWithRestResult(url, inviteOrganisation, Void.class);
    }

    @Override
    public RestResult<Void> saveInvites(List<ApplicationInviteResource> inviteResources) {
        String url = inviteRestUrl + "/save-invites";
        return postWithRestResult(url, inviteResources, Void.class);
    }

    @Override
    public RestResult<Void> saveKtaInvites(List<ApplicationKtaInviteResource> inviteResources) {
        String url = inviteRestUrl + "/save-kta-invites";
        return postWithRestResult(url, inviteResources, Void.class);
    }

    @Override
    public RestResult<Void> resendInvite(ApplicationInviteResource inviteResource) {
        String url = inviteRestUrl + "/resend-invite";
        return postWithRestResult(url, inviteResource, Void.class);
    }

    @Override
    public RestResult<Void> acceptInvite(String inviteHash, long userId) {
        String url = inviteRestUrl + String.format("/accept-invite/%s/%s", inviteHash, userId);
        return putWithRestResultAnonymous(url, Void.class);
    }

    @Override
    public RestResult<Void> acceptInvite(String inviteHash, long userId, long organisationId) {
        String url = inviteRestUrl + String.format("/accept-invite/%s/%s/%s", inviteHash, userId, organisationId);
        return putWithRestResultAnonymous(url, Void.class);
    }

    @Override
    public RestResult<Void> removeApplicationInvite(Long inviteId) {
        String url = inviteRestUrl + String.format("/remove-invite/%s", inviteId);
        return deleteWithRestResult(url);
    }

    @Override
    public RestResult<Boolean> checkExistingUser(String inviteHash) {
        String url = inviteRestUrl + String.format("/check-existing-user/%s", inviteHash);
        return getWithRestResultAnonymous(url, Boolean.class);
    }

    @Override
    public RestResult<UserResource> getUser(String inviteHash) {
        String url = inviteRestUrl + String.format(GET_USER_BY_HASH_MAPPING + "%s", inviteHash);
        return getWithRestResultAnonymous(url, UserResource.class);
    }

    @Override
    public RestResult<ApplicationInviteResource> getInviteByHash(String hash) {
        return getWithRestResultAnonymous(inviteRestUrl + "/get-invite-by-hash/" + hash, ApplicationInviteResource.class);
    }

    @Override
    public RestResult<InviteOrganisationResource> getInviteOrganisationByHash(String hash) {
        return getWithRestResultAnonymous(inviteRestUrl + "/get-invite-organisation-by-hash/" + hash, InviteOrganisationResource.class);
    }

    @Override
    public RestResult<List<InviteOrganisationResource>> getInvitesByApplication(Long applicationId) {
        String url = inviteRestUrl + "/get-invites-by-application-id/"+ applicationId;
        return getWithRestResult(url, inviteOrganisationResourceListType());
    }

}
