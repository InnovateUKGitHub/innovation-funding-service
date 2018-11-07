package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.longsListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.userListType;

/**
 * Implements {@link CompetitionSetupStakeholderRestService}
 */
@Service
public class CompetitionSetupStakeholderRestServiceImpl extends BaseRestService implements CompetitionSetupStakeholderRestService {

    private String competitionSetupStakeholderRestURL = "/competition/setup/";

    @Override
    public RestResult<Void> inviteStakeholder(InviteUserResource inviteUserResource, long competitionId) {
        return postWithRestResult(competitionSetupStakeholderRestURL + competitionId + "/stakeholder/invite", inviteUserResource, Void.class);
    }

    @Override
    public RestResult<List<UserResource>> findStakeholders(long competitionId) {
        return getWithRestResult(competitionSetupStakeholderRestURL + competitionId + "/stakeholder/find-all", userListType());
    }

    @Override
    public RestResult<Void> addStakeholder(long competitionId, long stakeholderUserId) {
        return postWithRestResult(competitionSetupStakeholderRestURL + competitionId + "/stakeholder/" + stakeholderUserId + "/add", Void.class);
    }

    @Override
    public RestResult<Void> removeStakeholder(long competitionId, long stakeholderUserId) {
        return postWithRestResult(competitionSetupStakeholderRestURL + competitionId + "/stakeholder/" + stakeholderUserId + "/remove", Void.class);
    }

    @Override
    public RestResult<List<UserResource>> findPendingStakeholderInvites(long competitionId) {
        return getWithRestResult(competitionSetupStakeholderRestURL + competitionId + "/stakeholder/pending-invites", userListType());
    }
}