package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.AvailableAssessorResource;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.innovateuk.ifs.invite.resource.CompetitionRejectionResource;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteResource;

import java.util.List;

/**
 * REST service for managing {@link org.innovateuk.ifs.invite.resource.InviteResource} to {@link org.innovateuk.ifs.competition.resource.CompetitionResource }
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
