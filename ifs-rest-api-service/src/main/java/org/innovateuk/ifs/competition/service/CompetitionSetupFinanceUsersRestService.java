package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.CompetitionFinanceInviteResource;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.registration.resource.CompetitionFinanceRegistrationResource;
import org.innovateuk.ifs.registration.resource.StakeholderRegistrationResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

public interface CompetitionSetupFinanceUsersRestService {

    RestResult<List<UserResource>> findCompetitionFinanceUsers(long competitionId);

    RestResult<List<UserResource>> findPendingCompetitionFinanceUsersInvites(long competitionId);

    RestResult<CompetitionFinanceInviteResource> getCompetitionFinanceInvite(String inviteHash);

    RestResult<Void> createFinanceUser(String inviteHash, CompetitionFinanceRegistrationResource competitionFinanceRegistrationResource);

    RestResult<Void> inviteFinanceUsers(InviteUserResource inviteUserResource, long competitionId);

    RestResult<Void> addFinanceUsers(long competitionId, long stakeholderUserId);

    RestResult<Void> removeFinanceUsers(long competitionId, long stakeholderUserId);
}
