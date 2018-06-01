package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.repository.ApplicationStatisticsRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionClosedKeyApplicationStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionFundedKeyApplicationStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionOpenKeyApplicationStatisticsResource;
import org.innovateuk.ifs.fundingdecision.domain.FundingDecisionStatus;
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

    @Override
    public ServiceResult<CompetitionOpenKeyApplicationStatisticsResource> getOpenKeyStatisticsByCompetition(
            long competitionId) {
        BigDecimal limit = new BigDecimal(50L);

        CompetitionOpenKeyApplicationStatisticsResource competitionOpenKeyApplicationStatisticsResource = new
                CompetitionOpenKeyApplicationStatisticsResource();
        competitionOpenKeyApplicationStatisticsResource.setApplicationsPerAssessor(competitionRepository.findById
                (competitionId).getAssessorCount());
        competitionOpenKeyApplicationStatisticsResource.setApplicationsStarted(applicationRepository
                .countByCompetitionIdAndApplicationProcessActivityStateInAndCompletionLessThanEqual(competitionId,
                        CREATED_AND_OPEN_STATUSES, limit));
        competitionOpenKeyApplicationStatisticsResource.setApplicationsPastHalf(applicationRepository
                .countByCompetitionIdAndApplicationProcessActivityStateNotInAndCompletionGreaterThan(competitionId,
                        SUBMITTED_AND_INELIGIBLE_STATES, limit));
        competitionOpenKeyApplicationStatisticsResource.setApplicationsSubmitted(applicationRepository
                .countByCompetitionIdAndApplicationProcessActivityStateIn(competitionId,
                        SUBMITTED_AND_INELIGIBLE_STATES));
        return serviceSuccess(competitionOpenKeyApplicationStatisticsResource);
    }

    @Override
    public ServiceResult<CompetitionClosedKeyApplicationStatisticsResource> getClosedKeyStatisticsByCompetition(
            long competitionId) {
        CompetitionClosedKeyApplicationStatisticsResource competitionClosedKeyApplicationStatisticsResource = new
                CompetitionClosedKeyApplicationStatisticsResource();
        competitionClosedKeyApplicationStatisticsResource.setApplicationsPerAssessor(competitionRepository.findById
                (competitionId).getAssessorCount());
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
        List<Application> applications = applicationRepository
                .findByCompetitionIdAndApplicationProcessActivityStateIn(competitionId, SUBMITTED_STATES);
        competitionFundedKeyApplicationStatisticsResource.setApplicationsSubmitted(applications.size());
        competitionFundedKeyApplicationStatisticsResource.setApplicationsFunded(getFundingDecisionCount(applications,
                FundingDecisionStatus.FUNDED));
        competitionFundedKeyApplicationStatisticsResource.setApplicationsNotFunded(getFundingDecisionCount
                (applications, FundingDecisionStatus.UNFUNDED));
        competitionFundedKeyApplicationStatisticsResource.setApplicationsOnHold(getFundingDecisionCount(applications,
                FundingDecisionStatus.ON_HOLD));
        competitionFundedKeyApplicationStatisticsResource.setApplicationsNotifiedOfDecision(applicationRepository
                .countByCompetitionIdAndFundingDecisionIsNotNullAndManageFundingEmailDateIsNotNull(competitionId));
        competitionFundedKeyApplicationStatisticsResource.setApplicationsAwaitingDecision(applicationRepository
                .countByCompetitionIdAndFundingDecisionIsNotNullAndManageFundingEmailDateIsNull(competitionId));

        return serviceSuccess(competitionFundedKeyApplicationStatisticsResource);
    }

    private int getFundingDecisionCount(List<Application> applications, FundingDecisionStatus fundingDecisionStatus) {
        return (int) applications.stream().filter(application -> {
            if (application.getFundingDecision() != null) {
                return application.getFundingDecision().equals(fundingDecisionStatus);
            }
            return false;
        }).count();
    }
}
