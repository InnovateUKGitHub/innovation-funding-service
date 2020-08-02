package org.innovateuk.ifs.grants.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource;
import org.innovateuk.ifs.grantsinvite.resource.SentGrantsInviteResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource} related data.
 */
public interface GrantsInviteRestService {
    RestResult<List<SentGrantsInviteResource>> getAllForProject(long projectId);
    RestResult<Void> invite(long projectId, GrantsInviteResource grantsInviteResource);
    RestResult<Void> resendInvite(long projectId, long inviteId);
    RestResult<Void> deleteInvite(long projectId, long inviteId);
    RestResult<SentGrantsInviteResource> getInviteByHash(long projectId, String hash);
    RestResult<Void> acceptInvite(long projectId, long inviteId);
}
