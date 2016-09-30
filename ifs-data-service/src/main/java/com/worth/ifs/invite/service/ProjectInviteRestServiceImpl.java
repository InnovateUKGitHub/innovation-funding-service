package com.worth.ifs.invite.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteProjectResource;
import com.worth.ifs.invite.resource.InviteResultsResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.inviteOrganisationResourceListType;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.inviteProjectResourceListType;
import static com.worth.ifs.invite.controller.InviteProjectController.*;

/**
 * A typical RestService to use as a client API on the web-service side for the data-service functionality .
 */
@Service
public class ProjectInviteRestServiceImpl extends BaseRestService implements ProjectInviteRestService {

    @Override
    public RestResult<Boolean> checkExistingUser(String inviteHash) {
        String url = PROJECT_INVITE_BASE_URL + CHECK_EXISTING_USER_URL + inviteHash;
        return getWithRestResultAnonymous(url, Boolean.class);
    }

    @Override
    public RestResult<UserResource> getUser(String inviteHash) {
        String url = PROJECT_INVITE_BASE_URL + GET_USER_BY_HASH_MAPPING + inviteHash;
        return getWithRestResultAnonymous(url, UserResource.class);
    }

    @Override
    public RestResult<InviteProjectResource> getInviteByHash(String hash) {
        String url = PROJECT_INVITE_BASE_URL + GET_INVITE_BY_HASH + hash;
        return getWithRestResultAnonymous(url, InviteProjectResource.class);
    }

    @Override
    public RestResult<Void> acceptInvite(String inviteHash, Long userId) {
        String url = PROJECT_INVITE_BASE_URL + ACCEPT_INVITE + inviteHash + "/" + userId;
        return putWithRestResultAnonymous(url, Void.class);
    }

    @Override
    public RestResult<Void> saveProjectInvite(InviteProjectResource inviteProjectResource) {

        String url = PROJECT_INVITE_BASE_URL + PROJECT_INVITE_SAVE;
        return postWithRestResult(url, inviteProjectResource, Void.class);
    }

    @Override
    public RestResult<List<InviteProjectResource>> getInvitesByProject (Long projectId){
        String url = PROJECT_INVITE_BASE_URL + GET_PROJECT_INVITE_LIST + projectId;
        return getWithRestResult(url, inviteProjectResourceListType());
    }

}
