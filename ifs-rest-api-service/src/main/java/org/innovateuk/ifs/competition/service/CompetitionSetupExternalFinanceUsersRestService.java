package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.CompetitionFinanceInviteResource;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.registration.resource.CompetitionFinanceRegistrationResource;
import org.innovateuk.ifs.registration.resource.StakeholderRegistrationResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

public interface CompetitionSetupExternalFinanceUsersRestService {

    RestResult<List<UserResource>> findExternalFinanceUsers(long competitionId);

    RestResult<List<UserResource>> findPendingExternalFinanceUsersInvites(long competitionId);

    RestResult<CompetitionFinanceInviteResource> getExternalFinanceInvite(String inviteHash);

    RestResult<Void> createExternalFinanceUser(String inviteHash, CompetitionFinanceRegistrationResource competitionFinanceRegistrationResource);

    RestResult<Void> inviteExternalFinanceUsers(InviteUserResource inviteUserResource, long competitionId);

    RestResult<Void> addExternalFinanceUsers(long competitionId, long stakeholderUserId);

    RestResult<Void> removeExternalFinanceUsers(long competitionId, long stakeholderUserId);
}
