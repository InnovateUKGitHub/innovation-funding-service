package com.worth.ifs.invite.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.resource.InviteProjectResource;
import com.worth.ifs.user.resource.UserResource;

import java.util.List;

public interface ProjectInviteRestService {
    RestResult<Boolean> checkExistingUser(String inviteHash);
    RestResult<UserResource> getUser(String inviteHash);
    RestResult<InviteProjectResource> getInviteByHash(String hash);
    RestResult<Void> acceptInvite(String inviteHash, Long userId);
    RestResult<Void> saveProjectInvite(InviteProjectResource inviteProjectResource);
    RestResult<List<InviteProjectResource>> getInvitesByProject (Long projectId);

}


