package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.resource.ApplicationStatus;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.CREATED_AND_OPEN_STATUSES;
import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.SUBMITTED_STATES;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class CompetitionSummaryServiceImpl extends BaseTransactionalService implements CompetitionSummaryService {

    @Autowired
    private CompetitionParticipantRepository competitionParticipantRepository;

    @Override
    public ServiceResult<CompetitionSummaryResource> getCompetitionSummaryByCompetitionId(Long competitionId) {
        Competition competition = competitionRepository.findById(competitionId);
        BigDecimal limit = new BigDecimal(50L);

        CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();
        competitionSummaryResource.setCompetitionId(competitionId);
        competitionSummaryResource.setCompetitionName(competition.getName());
        competitionSummaryResource.setCompetitionStatus(competition.getCompetitionStatus());
        competitionSummaryResource.setTotalNumberOfApplications(applicationRepository.countByCompetitionId(competitionId));
        competitionSummaryResource.setApplicationsStarted(
                applicationRepository.countByCompetitionIdAndApplicationProcessActivityStateStateInAndCompletionLessThanEqual(
                        competitionId, CREATED_AND_OPEN_STATUSES, limit
                )
        );
        competitionSummaryResource.setApplicationsInProgress(
                applicationRepository.countByCompetitionIdAndApplicationProcessActivityStateStateNotInAndCompletionGreaterThan(
                        competitionId, SUBMITTED_STATES, limit
                )
        );
        competitionSummaryResource.setApplicationsSubmitted(
                applicationRepository.countByCompetitionIdAndApplicationProcessActivityStateStateIn(competitionId, SUBMITTED_STATES)
        );
        competitionSummaryResource.setApplicationsNotSubmitted(
                competitionSummaryResource.getTotalNumberOfApplications() - competitionSummaryResource.getApplicationsSubmitted()
        );
        competitionSummaryResource.setApplicationDeadline(competition.getEndDate());
        competitionSummaryResource.setApplicationsFunded(
                applicationRepository.countByCompetitionIdAndApplicationProcessActivityStateState(competitionId, ApplicationStatus.APPROVED.toBackingState())
        );
        competitionSummaryResource.setAssessorsInvited(
                competitionParticipantRepository.countByCompetitionIdAndRole(competitionId, CompetitionParticipantRole.ASSESSOR)
        );
        competitionSummaryResource.setAssessorDeadline(competition.getAssessorDeadlineDate());

        return serviceSuccess(competitionSummaryResource);
    }
}
