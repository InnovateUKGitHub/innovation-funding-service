package org.innovateuk.ifs.assessment.dashboard.transactional;

import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.assessment.resource.dashboard.AssessorCompetitionDashboardResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.transactional.RootTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class AssessorCompetitionDashboardServiceImpl extends RootTransactionalService implements AssessorCompetitionDashboardService {

    @Autowired
    private ApplicationAssessmentService applicationAssessmentService;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Override
    public ServiceResult<AssessorCompetitionDashboardResource> getAssessorCompetitionDashboardResource(long userId, long competitionId) {
        List<ApplicationAssessmentResource> applicationAssessmentResource = applicationAssessmentService.getApplicationAssessmentResource(userId, competitionId).getSuccess();
        Competition competition = competitionRepository.findById(competitionId).get();

        AssessorCompetitionDashboardResource assessorCompetitionDashboardResource = new AssessorCompetitionDashboardResource(
                competitionId,
                competition.getName(),
                competition.getLeadTechnologist().getName(),
                competition.getAssessorAcceptsDate(),
                competition.getAssessorDeadlineDate(),
                applicationAssessmentResource);

        return serviceSuccess(assessorCompetitionDashboardResource);
    }
}