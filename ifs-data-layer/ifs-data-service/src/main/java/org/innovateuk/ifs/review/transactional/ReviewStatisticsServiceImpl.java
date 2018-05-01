package org.innovateuk.ifs.review.transactional;

import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.review.repository.ReviewInviteRepository;
import org.innovateuk.ifs.review.repository.ReviewParticipantRepository;
import org.innovateuk.ifs.review.resource.ReviewInviteStatisticsResource;
import org.innovateuk.ifs.review.resource.ReviewKeyStatisticsResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.SUBMITTED_STATES;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.PANEL_ASSESSOR;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Service to get statistics related to Review Panels.
 */
@Service
public class ReviewStatisticsServiceImpl implements ReviewStatisticsService {

    private ReviewInviteRepository reviewInviteRepository;
    private ReviewParticipantRepository reviewParticipantRepository;
    private ApplicationRepository applicationRepository;

    public ReviewStatisticsServiceImpl() {
    }

    @Autowired
    public ReviewStatisticsServiceImpl(ReviewInviteRepository reviewInviteRepository,
                                       ReviewParticipantRepository reviewParticipantRepository,
                                       ApplicationRepository applicationRepository) {
        this.reviewInviteRepository = reviewInviteRepository;
        this.reviewParticipantRepository = reviewParticipantRepository;
        this.applicationRepository = applicationRepository;
    }

    @Override
    public ServiceResult<ReviewKeyStatisticsResource> getReviewPanelKeyStatistics(long competitionId) {
        ReviewKeyStatisticsResource reviewKeyStatisticsResource = new ReviewKeyStatisticsResource();
        List<Long> assessmentPanelInviteIds = simpleMap(reviewInviteRepository.getByCompetitionId(competitionId), Invite::getId);

        reviewKeyStatisticsResource.setApplicationsInPanel(getApplicationPanelAssignedCountStatistic(competitionId));
        reviewKeyStatisticsResource.setAssessorsAccepted(getReviewParticipantCountStatistic(competitionId, ParticipantStatus.ACCEPTED, assessmentPanelInviteIds));
        reviewKeyStatisticsResource.setAssessorsPending(reviewInviteRepository.countByCompetitionIdAndStatusIn(competitionId, Collections.singleton(InviteStatus.SENT)));

        return serviceSuccess(reviewKeyStatisticsResource);
    }

    private int getApplicationPanelAssignedCountStatistic(long competitionId) {
        return applicationRepository.findByCompetitionIdAndApplicationProcessActivityStateInAndIdLike(
                competitionId, SUBMITTED_STATES, "",  null,true).size();
    }

    @Override
    public ServiceResult<ReviewInviteStatisticsResource> getReviewInviteStatistics(long competitionId) {
        List<Long> reviewPanelInviteIds = simpleMap(reviewInviteRepository.getByCompetitionId(competitionId), Invite::getId);

        int totalAssessorsInvited = reviewInviteRepository.countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT));
        int assessorsAccepted = getReviewParticipantCountStatistic(competitionId, ParticipantStatus.ACCEPTED, reviewPanelInviteIds);
        int assessorsDeclined = getReviewParticipantCountStatistic(competitionId, ParticipantStatus.REJECTED, reviewPanelInviteIds);

        return serviceSuccess(
                new ReviewInviteStatisticsResource(totalAssessorsInvited, assessorsAccepted, assessorsDeclined)
        );
    }

    private int getReviewParticipantCountStatistic(long competitionId, ParticipantStatus status, List<Long> inviteIds) {
        return reviewParticipantRepository.countByCompetitionIdAndRoleAndStatusAndInviteIdIn(competitionId, PANEL_ASSESSOR, status, inviteIds);
    }
}