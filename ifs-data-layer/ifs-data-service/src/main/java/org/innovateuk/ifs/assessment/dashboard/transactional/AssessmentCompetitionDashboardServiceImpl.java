package org.innovateuk.ifs.assessment.dashboard.transactional;

import org.innovateuk.ifs.assessment.period.domain.AssessmentPeriod;
import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.assessment.resource.dashboard.AssessorCompetitionDashboardResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
@Transactional(readOnly = true)
public class AssessmentCompetitionDashboardServiceImpl implements AssessmentCompetitionDashboardService {

    @Autowired
    private ApplicationAssessmentService applicationAssessmentService;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Override
    public ServiceResult<AssessorCompetitionDashboardResource> getAssessorCompetitionDashboardResource(long userId, long competitionId) {
        List<ApplicationAssessmentResource> assessments = applicationAssessmentService.getApplicationAssessmentResource(userId, competitionId).getSuccess();

        Competition competition = competitionRepository.findById(competitionId).get();
        String innovationLead = competition.getLeadTechnologist() == null ? "" : competition.getLeadTechnologist().getName();
        Optional<AssessmentPeriod> assessmentPeriod = Optional.ofNullable(competition.isAlwaysOpen() ?
                competition.getAssessmentPeriods().stream().filter(p -> p.isOpen()).sorted(Comparator.comparing(AssessmentPeriod::getId).reversed()).findFirst().orElse(null) : null);

        AssessorCompetitionDashboardResource assessorCompetitionDashboardResource = new AssessorCompetitionDashboardResource(
                competitionId,
                competition.getName(),
                innovationLead,
                competition.isAlwaysOpen(),
                assessmentPeriod.isPresent() ? assessmentPeriod.get().getId() : null,
                competition.getAssessorAcceptsDate(),
                competition.getAssessorDeadlineDate(),
                assessments);

        return serviceSuccess(assessorCompetitionDashboardResource);
    }

}