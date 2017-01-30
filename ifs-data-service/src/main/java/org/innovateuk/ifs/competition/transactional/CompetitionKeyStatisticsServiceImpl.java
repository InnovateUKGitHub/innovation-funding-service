package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.repository.ApplicationStatisticsRepository;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionClosedKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionInAssessmentKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionOpenKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionReadyToOpenKeyStatisticsResource;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.repository.CompetitionInviteRepository;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.EnumSet;

import static java.util.EnumSet.of;
import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.CREATED_AND_OPEN_STATUS_IDS;
import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.SUBMITTED_STATUS_IDS;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
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
    public ServiceResult<CompetitionReadyToOpenKeyStatisticsResource> getReadyToOpenKeyStatisticsByCompetition(long competitionId) {
        CompetitionReadyToOpenKeyStatisticsResource competitionKeyStatisticsResource = new CompetitionReadyToOpenKeyStatisticsResource();
        competitionKeyStatisticsResource.setAssessorsInvited(competitionInviteRepository.countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT)));
        competitionKeyStatisticsResource.setAssessorsAccepted(competitionParticipantRepository.countByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR, ParticipantStatus.ACCEPTED));
        return serviceSuccess(competitionKeyStatisticsResource);
    }

    @Override
    public ServiceResult<CompetitionOpenKeyStatisticsResource> getOpenKeyStatisticsByCompetition(long competitionId) {
        BigDecimal limit = new BigDecimal(50L);

        CompetitionOpenKeyStatisticsResource competitionKeyStatisticsResource = new CompetitionOpenKeyStatisticsResource();
        competitionKeyStatisticsResource.setAssessorsInvited(competitionInviteRepository.countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT)));
        competitionKeyStatisticsResource.setAssessorsAccepted(competitionParticipantRepository.countByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR, ParticipantStatus.ACCEPTED));
        competitionKeyStatisticsResource.setApplicationsPerAssessor(competitionRepository.findById(competitionId).getAssessorCount());
        competitionKeyStatisticsResource.setApplicationsStarted(applicationRepository.countByCompetitionIdAndApplicationStatusIdInAndCompletionLessThanEqual(competitionId, CREATED_AND_OPEN_STATUS_IDS, limit));
        competitionKeyStatisticsResource.setApplicationsPastHalf(applicationRepository.countByCompetitionIdAndApplicationStatusIdNotInAndCompletionGreaterThan(competitionId, SUBMITTED_STATUS_IDS, limit));
        competitionKeyStatisticsResource.setApplicationsSubmitted(applicationRepository.countByCompetitionIdAndApplicationStatusIdIn(competitionId, SUBMITTED_STATUS_IDS));
        return serviceSuccess(competitionKeyStatisticsResource);
    }

    @Override
    public ServiceResult<CompetitionClosedKeyStatisticsResource> getClosedKeyStatisticsByCompetition(long competitionId) {
        CompetitionClosedKeyStatisticsResource competitionKeyStatisticsResource = new CompetitionClosedKeyStatisticsResource();
        competitionKeyStatisticsResource.setApplicationsPerAssessor(competitionRepository.findById(competitionId).getAssessorCount());
        competitionKeyStatisticsResource.setApplicationsRequiringAssessors(applicationStatisticsRepository.findByCompetition(competitionId)
                .stream()
                .filter(as -> as.getAssessors() < competitionKeyStatisticsResource.getApplicationsPerAssessor())
                .count());
        competitionKeyStatisticsResource.setAssessorsWithoutApplications(competitionParticipantRepository.getByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR, ParticipantStatus.ACCEPTED)
                .stream()
                .filter(cp -> assessmentRepository.countByParticipantUserIdAndActivityStateStateNotIn(cp.getId(), of(REJECTED, WITHDRAWN)) == 0)
                .count());
        competitionKeyStatisticsResource.setAssignmentCount(applicationStatisticsRepository.findByCompetition(competitionId).stream().mapToLong(ApplicationStatistics::getAssessors).sum());
        competitionKeyStatisticsResource.setAssessorsInvited(competitionInviteRepository.countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT)));
        competitionKeyStatisticsResource.setAssessorsAccepted(competitionParticipantRepository.countByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR, ParticipantStatus.ACCEPTED));

        return serviceSuccess(competitionKeyStatisticsResource);
    }

    @Override
    public ServiceResult<CompetitionInAssessmentKeyStatisticsResource> getInAssessmentKeyStatisticsByCompetition(long competitionId) {
        CompetitionInAssessmentKeyStatisticsResource competitionKeyStatisticsResource = new CompetitionInAssessmentKeyStatisticsResource();
        competitionKeyStatisticsResource.setAssignmentCount(applicationStatisticsRepository.findByCompetition(competitionId).stream().mapToLong(ApplicationStatistics::getAssessors).sum());
        competitionKeyStatisticsResource.setAssignmentsWaiting(assessmentRepository.countByActivityStateStateAndTargetCompetitionId(PENDING, competitionId));
        competitionKeyStatisticsResource.setAssignmentsAccepted(assessmentRepository.countByActivityStateStateAndTargetCompetitionId(ACCEPTED, competitionId));
        competitionKeyStatisticsResource.setAssessmentsStarted(assessmentRepository.countByActivityStateStateInAndTargetCompetitionId(of(OPEN, DECIDE_IF_READY_TO_SUBMIT, READY_TO_SUBMIT), competitionId));
        competitionKeyStatisticsResource.setAssessmentsSubmitted(assessmentRepository.countByActivityStateStateAndTargetCompetitionId(SUBMITTED, competitionId));
        return serviceSuccess(competitionKeyStatisticsResource);
    }
}
