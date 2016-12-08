package com.worth.ifs.assessment.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.AvailableAssessorResource;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import com.worth.ifs.invite.resource.CompetitionRejectionResource;
import com.worth.ifs.invite.resource.ExistingUserStagedInviteResource;

import java.util.List;

/**
 * REST service for managing {@link com.worth.ifs.invite.resource.InviteResource} to {@link com.worth.ifs.competition.resource.CompetitionResource }
 */
public interface CompetitionInviteRestService {

    RestResult<CompetitionInviteResource> getInvite(String inviteHash);

    RestResult<CompetitionInviteResource> openInvite(String inviteHash);

    RestResult<Void> acceptInvite(String inviteHash);

    RestResult<Void> rejectInvite(String inviteHash, CompetitionRejectionResource rejectionReason);

    RestResult<Boolean> checkExistingUser(String inviteHash);

    RestResult<List<AvailableAssessorResource>> getAvailableAssessors(long competitionId);

    RestResult<CompetitionInviteResource> inviteUser(ExistingUserStagedInviteResource existingUserStagedInvite);

    RestResult<Void> deleteInvite(String email, long competitionId);
}
