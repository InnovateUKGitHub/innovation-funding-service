package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.projectInviteResourceListType;
import static org.innovateuk.ifs.invite.resource.ProjectInviteConstants.ACCEPT_INVITE;
import static org.innovateuk.ifs.invite.resource.ProjectInviteConstants.CHECK_EXISTING_USER_URL;
import static org.innovateuk.ifs.invite.resource.ProjectInviteConstants.GET_INVITE_BY_HASH;
import static org.innovateuk.ifs.invite.resource.ProjectInviteConstants.GET_PROJECT_INVITE_LIST;
import static org.innovateuk.ifs.invite.resource.ProjectInviteConstants.GET_USER_BY_HASH_MAPPING;
import static org.innovateuk.ifs.invite.resource.ProjectInviteConstants.PROJECT_INVITE_BASE_URL;
import static org.innovateuk.ifs.invite.resource.ProjectInviteConstants.PROJECT_INVITE_SAVE;

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
    public RestResult<ProjectUserInviteResource> getInviteByHash(String hash) {
        String url = PROJECT_INVITE_BASE_URL + GET_INVITE_BY_HASH + hash;
        return getWithRestResultAnonymous(url, ProjectUserInviteResource.class);
    }

    @Override
    public RestResult<Void> acceptInvite(String inviteHash, Long userId) {
        String url = PROJECT_INVITE_BASE_URL + ACCEPT_INVITE + inviteHash + "/" + userId;
        return putWithRestResultAnonymous(url, Void.class);
    }

    @Override
    public RestResult<Void> saveProjectInvite(ProjectUserInviteResource projectUserInviteResource) {
        String url = PROJECT_INVITE_BASE_URL + PROJECT_INVITE_SAVE;
        return postWithRestResult(url, projectUserInviteResource, Void.class);
    }

    @Override
    public RestResult<List<ProjectUserInviteResource>> getInvitesByProject (Long projectId){
        String url = PROJECT_INVITE_BASE_URL + GET_PROJECT_INVITE_LIST + projectId;
        return getWithRestResult(url, projectInviteResourceListType());
    }
}
