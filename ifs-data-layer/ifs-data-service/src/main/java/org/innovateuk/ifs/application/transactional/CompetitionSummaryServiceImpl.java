package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class CompetitionSummaryServiceImpl extends BaseTransactionalService implements CompetitionSummaryService {

    @Autowired
    private AssessmentParticipantRepository assessmentParticipantRepository;

    @Override
    public ServiceResult<CompetitionSummaryResource> getCompetitionSummaryByCompetitionId(Long competitionId) {
        Competition competition = competitionRepository.findById(competitionId).get();
        BigDecimal limit = new BigDecimal(50L);

        CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();
        competitionSummaryResource.setCompetitionId(competitionId);
        competitionSummaryResource.setCompetitionName(competition.getName());
        competitionSummaryResource.setCompetitionStatus(competition.getCompetitionStatus());
        competitionSummaryResource.setTotalNumberOfApplications(applicationRepository.countByCompetitionId(competitionId));
        competitionSummaryResource.setApplicationsStarted(
                applicationRepository.countByCompetitionIdAndApplicationProcessActivityStateInAndCompletionLessThanEqual(
                        competitionId, CREATED_AND_OPEN_STATUSES, limit
                )
        );
        competitionSummaryResource.setApplicationsInProgress(
                applicationRepository.countByCompetitionIdAndApplicationProcessActivityStateNotInAndCompletionGreaterThan(
                        competitionId, SUBMITTED_AND_INELIGIBLE_STATES, limit
                )
        );
        competitionSummaryResource.setApplicationsSubmitted(
                applicationRepository.countByCompetitionIdAndApplicationProcessActivityStateIn(competitionId, SUBMITTED_AND_INELIGIBLE_STATES)
        );
        competitionSummaryResource.setIneligibleApplications(
                applicationRepository.countByCompetitionIdAndApplicationProcessActivityStateIn(competitionId, INELIGIBLE_STATES)
        );
        competitionSummaryResource.setApplicationsNotSubmitted(
                competitionSummaryResource.getTotalNumberOfApplications() - competitionSummaryResource.getApplicationsSubmitted()
        );
        competitionSummaryResource.setApplicationDeadline(competition.getEndDate());
        competitionSummaryResource.setApplicationsFunded(
                applicationRepository.countByCompetitionIdAndApplicationProcessActivityState(competitionId, ApplicationState.APPROVED)
        );
        competitionSummaryResource.setAssessorsInvited(
                assessmentParticipantRepository.countByCompetitionIdAndRole(competitionId, CompetitionParticipantRole.ASSESSOR)
        );
        competitionSummaryResource.setAssessorDeadline(competition.getAssessorDeadlineDate());

        return serviceSuccess(competitionSummaryResource);
    }
}
