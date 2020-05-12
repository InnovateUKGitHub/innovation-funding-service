package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.CompetitionFinanceInviteResource;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.registration.resource.CompetitionFinanceRegistrationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.userListType;

@Service
public class CompetitionSetupExternalFinanceUsersRestServiceImpl extends BaseRestService implements CompetitionSetupExternalFinanceUsersRestService {

    private String competitionSetupFinanceUsersRestURL = "/competition/setup";

    @Override
    public RestResult<List<UserResource>> findExternalFinanceUsers(long competitionId) {
        return getWithRestResult(format("%s/%s/finance-users/find-all", competitionSetupFinanceUsersRestURL , competitionId), userListType());
    }

    @Override
    public RestResult<List<UserResource>> findPendingExternalFinanceUsersInvites(long competitionId) {
        return getWithRestResult(format("%s/%s/finance-users/pending-invites", competitionSetupFinanceUsersRestURL, competitionId), userListType());
    }

    @Override
    public RestResult<CompetitionFinanceInviteResource> getExternalFinanceInvite(String inviteHash) {
        return getWithRestResultAnonymous(format("%s/get-finance-users-invite/%s", competitionSetupFinanceUsersRestURL, inviteHash), CompetitionFinanceInviteResource.class);
    }

    @Override
    public RestResult<Void> createExternalFinanceUser(String inviteHash, CompetitionFinanceRegistrationResource competitionFinanceRegistrationResource) {
        return postWithRestResultAnonymous(format("%s/finance-users/create/%s", competitionSetupFinanceUsersRestURL, inviteHash), competitionFinanceRegistrationResource, Void.class);
    }

    @Override
    public RestResult<Void> inviteExternalFinanceUsers(InviteUserResource inviteUserResource, long competitionId) {
        return postWithRestResult(format("%s/%s/finance-users/invite", competitionSetupFinanceUsersRestURL, competitionId), inviteUserResource, Void.class);
    }

    @Override
    public RestResult<Void> addExternalFinanceUsers(long competitionId, long stakeholderUserId) {
        return postWithRestResult(format("%s/%s/finance-users/%s/add", competitionSetupFinanceUsersRestURL, competitionId, stakeholderUserId), Void.class);

    }

    @Override
    public RestResult<Void> removeExternalFinanceUsers(long competitionId, long stakeholderUserId) {
        return postWithRestResult(format("%s/%s/finance-users/%s/remove", competitionSetupFinanceUsersRestURL, competitionId, stakeholderUserId), Void.class);
    }
}
