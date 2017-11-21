package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ExternalInviteResource;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.invite.resource.RoleInvitePageResource;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.innovateuk.ifs.user.resource.SearchCategory;

import java.util.List;

/**
 * REST service for Invite User
 */
public interface InviteUserRestService {
    RestResult<Void> saveUserInvite(InviteUserResource inviteUserResource);
    RestResult<Boolean> checkExistingUser(String inviteHash);
    RestResult<RoleInviteResource> getInvite(String inviteHash);
    RestResult<RoleInvitePageResource> getPendingInternalUserInvites(int pageNumber, int pageSize);
    RestResult<List<ExternalInviteResource>> findExternalInvites(String searchString, SearchCategory searchCategory);
}



