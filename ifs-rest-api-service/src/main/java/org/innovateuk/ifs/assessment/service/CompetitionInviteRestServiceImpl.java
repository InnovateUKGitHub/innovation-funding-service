package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.AvailableAssessorResource;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.innovateuk.ifs.invite.resource.CompetitionRejectionResource;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.availableAssessorResourceListType;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static java.lang.String.format;

/**
 * REST service for managing {@link org.innovateuk.ifs.invite.resource.InviteResource} to {@link org.innovateuk.ifs.competition.resource.CompetitionResource}s
 */
@Service
public class CompetitionInviteRestServiceImpl extends BaseRestService implements CompetitionInviteRestService {

    private static final String competitionInviteRestUrl = "/competitioninvite";

    @Override
    public RestResult<CompetitionInviteResource> getInvite(String inviteHash) {
        return getWithRestResultAnonymous(format("%s/%s/%s", competitionInviteRestUrl, "/getInvite", inviteHash), CompetitionInviteResource.class);
    }

    @Override
    public RestResult<CompetitionInviteResource> openInvite(String inviteHash) {
        return postWithRestResultAnonymous(format("%s/%s/%s", competitionInviteRestUrl, "/openInvite", inviteHash), CompetitionInviteResource.class);
    }

    @Override
    public RestResult<Void> acceptInvite(String inviteHash) {
        return postWithRestResult(format("%s/%s/%s", competitionInviteRestUrl, "/acceptInvite", inviteHash), Void.class);
    }

    @Override
    public RestResult<Void> rejectInvite(String inviteHash, CompetitionRejectionResource rejectionReason) {
        return postWithRestResultAnonymous(format("%s/%s/%s", competitionInviteRestUrl, "/rejectInvite", inviteHash), rejectionReason, Void.class);
    }

    @Override
    public RestResult<Boolean> checkExistingUser(String inviteHash) {
        return getWithRestResultAnonymous(format("%s/%s/%s", competitionInviteRestUrl, "/checkExistingUser", inviteHash), Boolean.class);
    }

    @Override
    public RestResult<List<AvailableAssessorResource>> getAvailableAssessors(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", competitionInviteRestUrl, "/getAvailableAssessors", competitionId), availableAssessorResourceListType());
    }

    @Override
    public RestResult<CompetitionInviteResource> inviteUser(ExistingUserStagedInviteResource existingUserStagedInvite) {
        return postWithRestResult(format("%s/%s", competitionInviteRestUrl, "/inviteUser"), existingUserStagedInvite, CompetitionInviteResource.class);
    }

    @Override
    public RestResult<Void> deleteInvite(String email, long competitionId) {
        return postWithRestResult(format("%s/%s", competitionInviteRestUrl, "/deleteInvite"), asMap("email", email, "competitionId", competitionId), Void.class);
    }
}
