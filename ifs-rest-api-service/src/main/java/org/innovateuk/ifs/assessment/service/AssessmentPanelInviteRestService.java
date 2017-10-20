package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.*;

import java.util.List;

/**
 * REST service for managing {@link InviteResource} to {@link org.innovateuk.ifs.competition.resource.CompetitionResource }
 */

public interface AssessmentPanelInviteRestService {

    RestResult<AssessorInvitesToSendResource> getAllInvitesToSend(long competitionId);

    RestResult<Void> sendAllInvites(long competitionId, AssessorInviteSendResource assessorInviteSendResource);

    RestResult<AssessorCreatedInvitePageResource> getCreatedInvites(long competitionId, int page);

    RestResult<Void> inviteUsers(ExistingUserStagedInviteListResource existingUserStagedInvite);

    RestResult<AvailableAssessorPageResource> getAvailableAssessors(long competitionId, int page);

    RestResult<List<Long>> getAvailableAssessorIds(long competitionId);

    RestResult<CompetitionInviteResource> openInvite(String inviteHash);

    RestResult<Void> acceptInvite(String inviteHash);

    RestResult<Void> rejectInvite(String inviteHash, CompetitionRejectionResource rejectionReason);

    RestResult<Boolean> checkExistingUser(String inviteHash);
}
