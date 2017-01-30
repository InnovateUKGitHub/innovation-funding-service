package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.repository.ApplicationStatisticsRepository;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionKeyStatisticsResource;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.repository.CompetitionInviteRepository;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.workflow.resource.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.EnumSet;

import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.CREATED_AND_OPEN_STATUS_IDS;
import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.SUBMITTED_STATUS_IDS;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.domain.CompetitionParticipantRole.ASSESSOR;
import static org.innovateuk.ifs.workflow.resource.State.*;

@Service
public class CompetitionKeyStatisticsServiceImpl extends BaseTransactionalService implements CompetitionKeyStatisticsService {

    @Autowired
    CompetitionInviteRepository competitionInviteRepository;

    @Autowired
    ApplicationStatisticsRepository applicationStatisticsRepository;

    @Autowired
    AssessmentRepository assessmentRepository;

    @Autowired
    CompetitionParticipantRepository competitionParticipantRepository;

    @Override
    public ServiceResult<CompetitionKeyStatisticsResource> getKeyStatisticsByCompetition(long competitionId) {

        Competition competition = competitionRepository.findById(competitionId);
        BigDecimal limit = new BigDecimal(50L);

        CompetitionKeyStatisticsResource competitionKeyStatisticsResource = new CompetitionKeyStatisticsResource();
        competitionKeyStatisticsResource.setAssessorsInvited(competitionInviteRepository.countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT)));
        competitionKeyStatisticsResource.setAssessorsAccepted(competitionParticipantRepository.countByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR, ParticipantStatus.ACCEPTED));
        competitionKeyStatisticsResource.setApplicationsPerAssessor(competition.getAssessorCount());
        competitionKeyStatisticsResource.setApplicationsStarted(applicationRepository.countByCompetitionIdAndApplicationStatusIdInAndCompletionLessThanEqual(competitionId, CREATED_AND_OPEN_STATUS_IDS, limit));
        competitionKeyStatisticsResource.setApplicationsPastHalf(applicationRepository.countByCompetitionIdAndApplicationStatusIdNotInAndCompletionGreaterThan(competitionId, SUBMITTED_STATUS_IDS, limit));
        competitionKeyStatisticsResource.setApplicationsSubmitted(applicationRepository.countByCompetitionIdAndApplicationStatusIdIn(competitionId, SUBMITTED_STATUS_IDS));
        competitionKeyStatisticsResource.setApplicationsRequiringAssessors(applicationStatisticsRepository.findByCompetition(competitionId).stream().filter(as -> as.getAssessors() < competition.getAssessorCount()).count());
        competitionKeyStatisticsResource.setAssessorsWithoutApplications(-1L);
        competitionKeyStatisticsResource.setAssignmentCount(applicationStatisticsRepository.findByCompetition(competitionId).stream().mapToLong(ApplicationStatistics::getAssessors).sum());
        competitionKeyStatisticsResource.setAssignmentsWaiting(assessmentRepository.countByActivityStateStateAndTargetCompetitionId(State.CREATED, competitionId));
        competitionKeyStatisticsResource.setAssignmentsAccepted(assessmentRepository.countByActivityStateStateAndTargetCompetitionId(State.ACCEPTED,competitionId));
        competitionKeyStatisticsResource.setAssessmentsStarted(assessmentRepository.countByActivityStateStateInAndTargetCompetitionId(EnumSet.of(OPEN, DECIDE_IF_READY_TO_SUBMIT, READY_TO_SUBMIT), competitionId));
        competitionKeyStatisticsResource.setAssessmentsSubmitted(assessmentRepository.countByActivityStateStateAndTargetCompetitionId(SUBMITTED,competitionId));

        return serviceSuccess(competitionKeyStatisticsResource);
    }
}
