package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * REST service for managing {@link org.innovateuk.ifs.invite.resource.InviteResource} to {@link org.innovateuk.ifs.competition.resource.CompetitionResource }
 */
public interface CompetitionInviteRestService {

    RestResult<AssessorInvitesToSendResource> getAllInvitesToSend(long competitionId);

    RestResult<AssessorInvitesToSendResource> getInviteToSend(long inviteId);

    RestResult<AssessorInvitesToSendResource> getAllInvitesToResend(long competitionId, List<Long> inviteIds);

    RestResult<CompetitionInviteResource> getInvite(String inviteHash);

    RestResult<CompetitionInviteResource> openInvite(String inviteHash);

    RestResult<Void> acceptInvite(String inviteHash);

    RestResult<Void> rejectInvite(String inviteHash, CompetitionRejectionResource rejectionReason);

    RestResult<Boolean> checkExistingUser(String inviteHash);

    RestResult<AvailableAssessorPageResource> getAvailableAssessors(long competitionId, int page, Optional<Long> innovationArea);

    RestResult<List<Long>> getAvailableAssessorIds(long competitionId, Optional<Long> innovationArea);

    RestResult<List<Long>> getAssessorsNotAcceptedInviteIds(long competitionId,
                                                            Optional<Long> innovationArea,
                                                            List<ParticipantStatusResource> participantStatus,
                                                            Optional<Boolean> compliant);

    RestResult<AssessorCreatedInvitePageResource> getCreatedInvites(long competitionId, int page);

    RestResult<AssessorInviteOverviewPageResource> getInvitationOverview(long competitionId,
                                                                         int page,
                                                                         Optional<Long> innovationArea,
                                                                         List<ParticipantStatusResource> participantStatus,
                                                                         Optional<Boolean> compliant);

    RestResult<CompetitionInviteStatisticsResource> getInviteStatistics(long competitionId);

    RestResult<CompetitionInviteResource> inviteUser(ExistingUserStagedInviteResource existingUserStagedInvite);

    RestResult<Void> inviteUsers(ExistingUserStagedInviteListResource existingUserStagedInvite);

    RestResult<Void> inviteNewUsers(NewUserStagedInviteListResource newUserStagedInvites, long competitionId);

    RestResult<Void> deleteInvite(String email, long competitionId);

    RestResult<Void> deleteAllInvites(long competitionId);

    RestResult<Void> sendAllInvites(long competitionId, AssessorInviteSendResource assessorInviteSendResource);

    RestResult<Void> resendInvite(long inviteId, AssessorInviteSendResource assessorInviteSendResource);

    RestResult<Void> resendInvites(List<Long> inviteIds, AssessorInviteSendResource assessorInviteSendResource);

}
