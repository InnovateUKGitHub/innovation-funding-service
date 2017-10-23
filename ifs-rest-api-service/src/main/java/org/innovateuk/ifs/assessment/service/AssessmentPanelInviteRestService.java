package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.*;

import java.util.List;

/**
 * REST service for managing {@link InviteResource} to {@link org.innovateuk.ifs.competition.resource.CompetitionResource }
 */
public interface AssessmentPanelInviteRestService {

    RestResult<AssessorInvitesToSendResource> getAllInvitesToSend(long competitionId);

    RestResult<AssessorInvitesToSendResource> getAllInvitesToResend(long competitionId, List<Long> inviteIds);

    RestResult<Void> sendAllInvites(long competitionId, AssessorInviteSendResource assessorInviteSendResource);

    RestResult<Void> resendInvites(List<Long> inviteIds, AssessorInviteSendResource assessorInviteSendResource);

    RestResult<AssessorCreatedInvitePageResource> getCreatedInvites(long competitionId, int page);

    RestResult<Void> inviteUsers(ExistingUserStagedInviteListResource existingUserStagedInvite);

    RestResult<AvailableAssessorPageResource> getAvailableAssessors(long competitionId, int page);

    RestResult<List<Long>> getAvailableAssessorIds(long competitionId);

    RestResult<List<Long>> getNonAcceptedAssessorInviteIds(long competitionId);

    RestResult<AssessorInviteOverviewPageResource> getInvitationOverview(long competitionId,
                                                                         int page,
                                                                         List<ParticipantStatusResource> participantStatus);

    RestResult<AssessmentPanelInviteResource> openInvite(String inviteHash);

    RestResult<Void> acceptInvite(String inviteHash);

    RestResult<Void> rejectInvite(String inviteHash, CompetitionRejectionResource rejectionReason);

    RestResult<Boolean> checkExistingUser(String inviteHash);
}
