package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.invite.resource.StakeholderInviteResource;
import org.innovateuk.ifs.registration.resource.StakeholderRegistrationResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

/**
 * Interface for CRUD operations on Stakeholder related data.
 */
public interface CompetitionSetupStakeholderRestService {

    RestResult<Void> inviteStakeholder(InviteUserResource inviteUserResource, long competitionId);

    RestResult<List<UserResource>> findStakeholders(long competitionId);

    RestResult<StakeholderInviteResource> getInvite(String inviteHash);

    RestResult<Void> createStakeholder(String inviteHash, StakeholderRegistrationResource stakeholderRegistrationResource);

    RestResult<Void> addStakeholder(long competitionId, long stakeholderUserId);

    RestResult<Void> removeStakeholder(long competitionId, long stakeholderUserId);

    RestResult<List<UserResource>> findPendingStakeholderInvites(long competitionId);
}

