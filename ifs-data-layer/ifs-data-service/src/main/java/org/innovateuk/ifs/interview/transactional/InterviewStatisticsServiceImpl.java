package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.repository.InterviewInviteRepository;
import org.innovateuk.ifs.interview.repository.InterviewParticipantRepository;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.interview.resource.InterviewInviteStatisticsResource;
import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.INTERVIEW_ASSESSOR;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapSet;

/**
 * Service to get statistics related to Interview Panels.
 */
@Service
public class InterviewStatisticsServiceImpl implements InterviewStatisticsService{

    private InterviewInviteRepository interviewInviteRepository;
    private InterviewParticipantRepository interviewParticipantRepository;
    private ApplicationRepository applicationRepository;
    private InterviewAssignmentRepository interviewAssignmentRepository;

    public InterviewStatisticsServiceImpl() {
    }

    @Autowired
    public InterviewStatisticsServiceImpl(InterviewInviteRepository interviewInviteRepository,
                                          InterviewParticipantRepository interviewParticipantRepository,
                                          ApplicationRepository applicationRepository,
                                          InterviewAssignmentRepository interviewAssignmentRepository) {
        this.interviewInviteRepository = interviewInviteRepository;
        this.interviewParticipantRepository = interviewParticipantRepository;
        this.applicationRepository = applicationRepository;
        this.interviewAssignmentRepository = interviewAssignmentRepository;
    }

    @Override
    public ServiceResult<InterviewAssignmentKeyStatisticsResource> getInterviewPanelKeyStatistics(long competitionId) {
        int applicationsInCompetition = applicationRepository.countByCompetitionIdAndApplicationProcessActivityStateState(competitionId, ApplicationState.SUBMITTED.getBackingState());
        int applicationsAssigned = interviewAssignmentRepository.
                countByTargetCompetitionIdAndActivityStateStateIn(competitionId,
                        simpleMapSet(asList(InterviewAssignmentState.ASSIGNED_STATES), InterviewAssignmentState::getBackingState)
                );

        return serviceSuccess(new InterviewAssignmentKeyStatisticsResource(applicationsInCompetition, applicationsAssigned));
    }

    @Override
    public ServiceResult<InterviewInviteStatisticsResource> getInterviewInviteStatistics(long competitionId) {
        List<Long> interviewPanelInviteIds = simpleMap(interviewInviteRepository.getByCompetitionId(competitionId), Invite::getId);

        int totalAssessorsInvited = interviewInviteRepository.countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT));
        int assessorsAccepted = getInterviewParticipantCountStatistic(competitionId, ParticipantStatus.ACCEPTED, interviewPanelInviteIds);
        int assessorsDeclined = getInterviewParticipantCountStatistic(competitionId, ParticipantStatus.REJECTED, interviewPanelInviteIds);

        return serviceSuccess(
                new InterviewInviteStatisticsResource(
                        totalAssessorsInvited,
                        assessorsAccepted,
                        assessorsDeclined
                )
        );
    }

    private int getInterviewParticipantCountStatistic(long competitionId, ParticipantStatus status, List<Long> inviteIds) {
        return interviewParticipantRepository.countByCompetitionIdAndRoleAndStatusAndInviteIdIn(competitionId, INTERVIEW_ASSESSOR, status, inviteIds);
    }
}