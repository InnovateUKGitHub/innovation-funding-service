package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

public interface ProjectInviteRestService {
    RestResult<Boolean> checkExistingUser(String inviteHash);
    RestResult<UserResource> getUser(String inviteHash);
    RestResult<ProjectUserInviteResource> getInviteByHash(String hash);
    RestResult<Void> acceptInvite(String inviteHash, Long userId);
    RestResult<Void> saveProjectInvite(ProjectUserInviteResource projectUserInviteResource);
    RestResult<List<ProjectUserInviteResource>> getInvitesByProject (Long projectId);
}


