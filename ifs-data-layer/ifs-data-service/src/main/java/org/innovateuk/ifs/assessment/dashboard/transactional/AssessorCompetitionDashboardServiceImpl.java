package org.innovateuk.ifs.assessment.dashboard.transactional;

import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.assessment.resource.dashboard.AssessorCompetitionDashboardResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class AssessorCompetitionDashboardServiceImpl extends BaseTransactionalService implements AssessorCompetitionDashboardService {

    @Override
    public ServiceResult<AssessorCompetitionDashboardResource> getAssessorCompetitionDashboardResource(long competitionId, List<ApplicationAssessmentResource> applicationAssessmentResource) {
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
