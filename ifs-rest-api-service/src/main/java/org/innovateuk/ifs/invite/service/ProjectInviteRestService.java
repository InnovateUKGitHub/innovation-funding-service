package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ProjectInviteResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

public interface ProjectInviteRestService {
    RestResult<Boolean> checkExistingUser(String inviteHash);
    RestResult<UserResource> getUser(String inviteHash);
    RestResult<ProjectInviteResource> getInviteByHash(String hash);
    RestResult<Void> acceptInvite(String inviteHash, Long userId);
    RestResult<Void> saveProjectInvite(ProjectInviteResource projectInviteResource);
    RestResult<List<ProjectInviteResource>> getInvitesByProject (Long projectId);
}


