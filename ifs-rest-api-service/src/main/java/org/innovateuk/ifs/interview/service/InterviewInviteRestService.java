package org.innovateuk.ifs.interview.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.interview.resource.InterviewAssessorAllocateApplicationsPageResource;
import org.innovateuk.ifs.invite.resource.*;

import java.util.List;

/**
 * REST service for managing {@link InviteResource} to {@link org.innovateuk.ifs.competition.resource.CompetitionResource }
 */
public interface InterviewInviteRestService {

    RestResult<AssessorInvitesToSendResource> getAllInvitesToSend(long competitionId);

    RestResult<AssessorInvitesToSendResource> getAllInvitesToResend(long competitionId, List<Long> inviteIds);

    RestResult<Void> sendAllInvites(long competitionId, AssessorInviteSendResource assessorInviteSendResource);

    RestResult<Void> resendInvites(List<Long> inviteIds, AssessorInviteSendResource assessorInviteSendResource);

    RestResult<AssessorCreatedInvitePageResource> getCreatedInvites(long competitionId, int page);

    RestResult<Void> inviteUsers(ExistingUserStagedInviteListResource existingUserStagedInvite);

    RestResult<AvailableAssessorPageResource> getAvailableAssessors(long competitionId, int page);

    RestResult<List<Long>> getAvailableAssessorIds(long competitionId);

    RestResult<List<InterviewParticipantResource>> getAllInvitesByUser(long userId);

    RestResult<List<Long>> getNonAcceptedAssessorInviteIds(long competitionId);

    RestResult<AssessorInviteOverviewPageResource> getInvitationOverview(long competitionId,
                                                                         int page,
                                                                         List<ParticipantStatusResource> participantStatus);

    RestResult<InterviewAssessorAllocateApplicationsPageResource> getAllocateApplicationsOverview(long competitionId, int page);

    RestResult<InterviewInviteResource> openInvite(String inviteHash);

    RestResult<Void> acceptInvite(String inviteHash);

    RestResult<Void> rejectInvite(String inviteHash);

    RestResult<Boolean> checkExistingUser(String inviteHash);

    RestResult<Void> deleteInvite(String email, long competitionId);

    RestResult<Void> deleteAllInvites(long competitionId);
}
