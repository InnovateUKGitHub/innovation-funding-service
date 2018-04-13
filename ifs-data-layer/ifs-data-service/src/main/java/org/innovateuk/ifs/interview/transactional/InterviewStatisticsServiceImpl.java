package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.interview.repository.InterviewInviteRepository;
import org.innovateuk.ifs.interview.repository.InterviewParticipantRepository;
import org.innovateuk.ifs.interview.resource.InterviewInviteStatisticsResource;
import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.INTERVIEW_ASSESSOR;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Service
public class InterviewStatisticsServiceImpl implements InterviewStatisticsService{

    private InterviewInviteRepository interviewInviteRepository;
    private InterviewParticipantRepository interviewParticipantRepository;


    public InterviewStatisticsServiceImpl() {
    }

    @Autowired
    public InterviewStatisticsServiceImpl(InterviewInviteRepository interviewInviteRepository,
                                          InterviewParticipantRepository interviewParticipantRepository) {
        this.interviewInviteRepository = interviewInviteRepository;
        this.interviewParticipantRepository = interviewParticipantRepository;
    }

    @Override
    public ServiceResult<InterviewInviteStatisticsResource> getInterviewInviteStatistics(long competitionId) {
        List<Long> reviewPanelInviteIds = simpleMap(interviewInviteRepository.getByCompetitionId(competitionId), Invite::getId);

        int totalAssessorsInvited = interviewInviteRepository.countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT));
        int assessorsAccepted = getInterviewParticipantCountStatistic(competitionId, ParticipantStatus.ACCEPTED, reviewPanelInviteIds);
        int assessorsDeclined = getInterviewParticipantCountStatistic(competitionId, ParticipantStatus.REJECTED, reviewPanelInviteIds);

        return serviceSuccess(
                new InterviewInviteStatisticsResource(
                        totalAssessorsInvited,
                        assessorsAccepted,
                        assessorsDeclined,
                        totalAssessorsInvited - assessorsAccepted - assessorsDeclined)
        );
    }


    private int getInterviewParticipantCountStatistic(long competitionId, ParticipantStatus status, List<Long> inviteIds) {
        return interviewParticipantRepository.countByCompetitionIdAndRoleAndStatusAndInviteIdIn(competitionId, INTERVIEW_ASSESSOR, status, inviteIds);
    }
}