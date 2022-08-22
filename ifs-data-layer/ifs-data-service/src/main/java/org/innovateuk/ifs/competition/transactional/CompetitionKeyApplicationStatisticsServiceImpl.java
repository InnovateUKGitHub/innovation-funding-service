package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.repository.ApplicationStatisticsRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionAssessmentConfigRepository;
import org.innovateuk.ifs.competition.resource.CompetitionClosedKeyApplicationStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionEoiKeyApplicationStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionFundedKeyApplicationStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionOpenKeyApplicationStatisticsResource;
import org.innovateuk.ifs.fundingdecision.domain.DecisionStatus;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class CompetitionKeyApplicationStatisticsServiceImpl extends BaseTransactionalService implements
        CompetitionKeyApplicationStatisticsService {

    @Autowired
    private ApplicationStatisticsRepository applicationStatisticsRepository;

    @Autowired
    private CompetitionAssessmentConfigRepository competitionAssessmentConfigRepository;

    @Override
    public ServiceResult<CompetitionOpenKeyApplicationStatisticsResource> getOpenKeyStatisticsByCompetition(
            long competitionId) {
        BigDecimal limit = new BigDecimal(50L);

        CompetitionOpenKeyApplicationStatisticsResource competitionOpenKeyApplicationStatisticsResource = new
                CompetitionOpenKeyApplicationStatisticsResource();
        competitionOpenKeyApplicationStatisticsResource.setApplicationsPerAssessor(competitionAssessmentConfigRepository.findOneByCompetitionId
                (competitionId).get().getAssessorCount());

        Competition competition = competitionRepository.findById(competitionId).get();
        if(competition.isEnabledForPreRegistration()) {
            competitionOpenKeyApplicationStatisticsResource.setApplicationsStarted(applicationRepository
                    .countStartedApplicationsByCompetitionId(competitionId));
            competitionOpenKeyApplicationStatisticsResource.setApplicationsPastHalf(applicationRepository
                    .countInProgressApplicationsByCompetitionId(competitionId));
            competitionOpenKeyApplicationStatisticsResource.setApplicationsSubmitted(applicationRepository
                    .countApplicationsByCompetitionIdAndStateIn(competitionId, SUBMITTED_AND_INELIGIBLE_STATES));
        } else {
            competitionOpenKeyApplicationStatisticsResource.setApplicationsStarted(applicationRepository
                    .countByCompetitionIdAndApplicationProcessActivityStateInAndCompletionLessThanEqual(competitionId,
                            CREATED_AND_OPEN_STATUSES, limit));
            competitionOpenKeyApplicationStatisticsResource.setApplicationsPastHalf(applicationRepository
                    .countByCompetitionIdAndApplicationProcessActivityStateNotInAndCompletionGreaterThan(competitionId,
                            SUBMITTED_AND_INELIGIBLE_STATES, limit));
            competitionOpenKeyApplicationStatisticsResource.setApplicationsSubmitted(applicationRepository
                    .countByCompetitionIdAndApplicationProcessActivityStateIn(competitionId,
                            SUBMITTED_AND_INELIGIBLE_STATES));
        }
        return serviceSuccess(competitionOpenKeyApplicationStatisticsResource);
    }

    @Override
    public ServiceResult<CompetitionClosedKeyApplicationStatisticsResource> getClosedKeyStatisticsByCompetition(
            long competitionId) {
        CompetitionClosedKeyApplicationStatisticsResource competitionClosedKeyApplicationStatisticsResource = new
                CompetitionClosedKeyApplicationStatisticsResource();
        competitionClosedKeyApplicationStatisticsResource.setApplicationsPerAssessor(competitionAssessmentConfigRepository.findOneByCompetitionId
                (competitionId).get().getAssessorCount());
        competitionClosedKeyApplicationStatisticsResource.setApplicationsRequiringAssessors
                (applicationStatisticsRepository.findByCompetitionAndApplicationProcessActivityStateIn(competitionId,
                        SUBMITTED_STATES)
                        .stream()
                        .filter(as -> as.getAssessors() < competitionClosedKeyApplicationStatisticsResource
                                .getApplicationsPerAssessor())
                        .mapToInt(e -> 1)
                        .sum());
        competitionClosedKeyApplicationStatisticsResource.setAssignmentCount(applicationStatisticsRepository
                .findByCompetitionAndApplicationProcessActivityStateIn(competitionId, SUBMITTED_STATES).stream()
                .mapToInt(ApplicationStatistics::getAssessors).sum());

        return serviceSuccess(competitionClosedKeyApplicationStatisticsResource);
    }

    @Override
    public ServiceResult<CompetitionFundedKeyApplicationStatisticsResource> getFundedKeyStatisticsByCompetition(
            long competitionId) {
        CompetitionFundedKeyApplicationStatisticsResource competitionFundedKeyApplicationStatisticsResource = new
                CompetitionFundedKeyApplicationStatisticsResource();

        Competition competition = competitionRepository.findById(competitionId).get();
        if(competition.isEnabledForPreRegistration()) {
            competitionFundedKeyApplicationStatisticsResource.setApplicationsSubmitted(applicationRepository
                    .countApplicationsByCompetitionIdAndStateIn(competitionId, SUBMITTED_AND_INELIGIBLE_STATES));
        } else {
            competitionFundedKeyApplicationStatisticsResource
                    .setApplicationsSubmitted(applicationRepository.countByCompetitionIdAndApplicationProcessActivityStateIn(competitionId, SUBMITTED_STATES));
        }
        competitionFundedKeyApplicationStatisticsResource.setApplicationsFunded(applicationRepository.countByCompetitionIdAndDecision(competitionId, DecisionStatus.FUNDED));
        competitionFundedKeyApplicationStatisticsResource.setApplicationsNotFunded(applicationRepository.countByCompetitionIdAndDecision(competitionId, DecisionStatus.UNFUNDED));
        competitionFundedKeyApplicationStatisticsResource.setApplicationsOnHold(applicationRepository.countByCompetitionIdAndDecision(competitionId, DecisionStatus.ON_HOLD));
        competitionFundedKeyApplicationStatisticsResource.setApplicationsNotifiedOfDecision(applicationRepository
                .countByDecidedAndSentApplications(competitionId));
        competitionFundedKeyApplicationStatisticsResource.setApplicationsAwaitingDecision(applicationRepository
                .countByDecidedAndAwaitSendApplications(competitionId));

        return serviceSuccess(competitionFundedKeyApplicationStatisticsResource);
    }

    @Override
    public ServiceResult<CompetitionEoiKeyApplicationStatisticsResource> getEoiKeyStatisticsByCompetition(long competitionId) {
        CompetitionEoiKeyApplicationStatisticsResource competitionEoiKeyApplicationStatisticsResource = new CompetitionEoiKeyApplicationStatisticsResource();

        int eoiApplicationsSubmitted = applicationRepository.countByCompetitionIdAndApplicationExpressionOfInterestConfigEnabledForExpressionOfInterestTrueAndApplicationProcessActivityStateIn(competitionId, SUBMITTED_STATES);
        competitionEoiKeyApplicationStatisticsResource.setEOISubmitted(eoiApplicationsSubmitted);
        competitionEoiKeyApplicationStatisticsResource.setEOISuccessful(applicationRepository.countByCompetitionIdAndDecision(competitionId, DecisionStatus.EOI_APPROVED));
        competitionEoiKeyApplicationStatisticsResource.setEOIUnsuccessful(applicationRepository.countByCompetitionIdAndDecision(competitionId, DecisionStatus.EOI_REJECTED));
        competitionEoiKeyApplicationStatisticsResource.setEOINotifiedOfDecision(applicationRepository
                .countByDecidedAndSentEOI(competitionId));
        competitionEoiKeyApplicationStatisticsResource.setEOIAwaitingDecision(applicationRepository
                .countByDecidedAndAwaitSendEOI(competitionId));

        return serviceSuccess(competitionEoiKeyApplicationStatisticsResource);
    }

    private int getDecisionCount(List<Application> applications, DecisionStatus DecisionStatus) {
        return (int) applications.stream().filter(application -> {
            if (application.getDecision() != null) {
                return application.getDecision().equals(DecisionStatus);
            }
            return false;
        }).count();
    }
}
