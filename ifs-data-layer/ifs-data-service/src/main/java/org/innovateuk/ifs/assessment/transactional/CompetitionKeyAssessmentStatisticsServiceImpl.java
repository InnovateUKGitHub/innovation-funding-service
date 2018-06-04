package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.repository.ApplicationStatisticsRepository;
import org.innovateuk.ifs.assessment.repository.AssessmentInviteRepository;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.assessment.resource.CompetitionClosedKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionOpenKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionReadyToOpenKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

import static java.util.EnumSet.of;
import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.SUBMITTED_STATES;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.ASSESSOR;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;

@Service
public class CompetitionKeyAssessmentStatisticsServiceImpl extends BaseTransactionalService implements
        CompetitionKeyAssessmentStatisticsService {

    @Autowired
    private AssessmentInviteRepository assessmentInviteRepository;

    @Autowired
    private ApplicationStatisticsRepository applicationStatisticsRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private AssessmentParticipantRepository assessmentParticipantRepository;

    @Override
    public ServiceResult<CompetitionReadyToOpenKeyAssessmentStatisticsResource>
    getReadyToOpenKeyStatisticsByCompetition(
            long competitionId) {
        CompetitionReadyToOpenKeyAssessmentStatisticsResource competitionKeyStatisticsResource = new
                CompetitionReadyToOpenKeyAssessmentStatisticsResource();
        competitionKeyStatisticsResource.setAssessorsInvited(assessmentInviteRepository
                .countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT)));
        competitionKeyStatisticsResource.setAssessorsAccepted(assessmentParticipantRepository
                .countByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR, ParticipantStatus.ACCEPTED));
        return serviceSuccess(competitionKeyStatisticsResource);
    }

    @Override
    public ServiceResult<CompetitionOpenKeyAssessmentStatisticsResource> getOpenKeyStatisticsByCompetition(
            long competitionId) {
        CompetitionOpenKeyAssessmentStatisticsResource competitionOpenKeyAssessmentStatisticsResource = new
                CompetitionOpenKeyAssessmentStatisticsResource();
        competitionOpenKeyAssessmentStatisticsResource.setAssessorsInvited(assessmentInviteRepository
                .countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT)));
        competitionOpenKeyAssessmentStatisticsResource.setAssessorsAccepted(assessmentParticipantRepository
                .countByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR, ParticipantStatus.ACCEPTED));

        return serviceSuccess(competitionOpenKeyAssessmentStatisticsResource);
    }

    @Override
    public ServiceResult<CompetitionClosedKeyAssessmentStatisticsResource> getClosedKeyStatisticsByCompetition(
            long competitionId) {
        CompetitionClosedKeyAssessmentStatisticsResource competitionClosedKeyAssessmentStatisticsResource = new
                CompetitionClosedKeyAssessmentStatisticsResource();
        competitionClosedKeyAssessmentStatisticsResource.setAssessorsWithoutApplications(assessmentParticipantRepository
                .getByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR, ParticipantStatus.ACCEPTED)
                .stream()
                .filter(cp -> assessmentRepository.countByParticipantUserIdAndActivityStateNotIn(cp.getId(), of
                        (REJECTED, WITHDRAWN)) == 0)
                .mapToInt(e -> 1)
                .sum());
        competitionClosedKeyAssessmentStatisticsResource.setAssessorsInvited(assessmentInviteRepository
                .countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT)));
        competitionClosedKeyAssessmentStatisticsResource.setAssessorsAccepted(assessmentParticipantRepository
                .countByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR, ParticipantStatus.ACCEPTED));

        return serviceSuccess(competitionClosedKeyAssessmentStatisticsResource);
    }

    @Override
    public ServiceResult<CompetitionInAssessmentKeyAssessmentStatisticsResource>
    getInAssessmentKeyStatisticsByCompetition(long competitionId) {
        CompetitionInAssessmentKeyAssessmentStatisticsResource competitionInAssessmentKeyAssessmentStatisticsResource
                = new CompetitionInAssessmentKeyAssessmentStatisticsResource();
        competitionInAssessmentKeyAssessmentStatisticsResource.setAssignmentCount(applicationStatisticsRepository
                .findByCompetitionAndApplicationProcessActivityStateIn(competitionId, SUBMITTED_STATES).stream()
                .mapToInt(ApplicationStatistics::getAssessors).sum());
        competitionInAssessmentKeyAssessmentStatisticsResource.setAssignmentsWaiting(assessmentRepository
                .countByActivityStateAndTargetCompetitionId(PENDING, competitionId));
        competitionInAssessmentKeyAssessmentStatisticsResource.setAssignmentsAccepted(assessmentRepository
                .countByActivityStateAndTargetCompetitionId(ACCEPTED, competitionId));
        competitionInAssessmentKeyAssessmentStatisticsResource.setAssessmentsStarted(assessmentRepository
                .countByActivityStateInAndTargetCompetitionId(of(OPEN, DECIDE_IF_READY_TO_SUBMIT, READY_TO_SUBMIT),
                        competitionId));
        competitionInAssessmentKeyAssessmentStatisticsResource.setAssessmentsSubmitted(assessmentRepository
                .countByActivityStateAndTargetCompetitionId(SUBMITTED, competitionId));

        return serviceSuccess(competitionInAssessmentKeyAssessmentStatisticsResource);
    }
}
