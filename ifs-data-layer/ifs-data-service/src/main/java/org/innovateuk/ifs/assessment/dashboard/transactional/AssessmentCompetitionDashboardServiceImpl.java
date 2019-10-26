package org.innovateuk.ifs.assessment.dashboard.transactional;

import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.assessment.resource.dashboard.AssessorCompetitionDashboardResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class AssessmentCompetitionDashboardServiceImpl implements AssessmentCompetitionDashboardService {

    @Autowired
    private ApplicationAssessmentService applicationAssessmentService;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Override
    public ServiceResult<AssessorCompetitionDashboardResource> getAssessorCompetitionDashboardResource(long userId, long competitionId) {
        Set<AssessmentState> allowedStates = EnumSet.of(PENDING, ACCEPTED, OPEN, READY_TO_SUBMIT, SUBMITTED);

        List<ApplicationAssessmentResource> assessments = applicationAssessmentService.getApplicationAssessmentResource(userId, competitionId).getSuccess()
                .stream()
                .filter(assessment -> allowedStates.contains(assessment.getState()))
                .collect(toList());

        Competition competition = competitionRepository.findById(competitionId).get();
        AssessorCompetitionDashboardResource assessorCompetitionDashboardResource = new AssessorCompetitionDashboardResource(
                competitionId,
                competition.getName(),
                competition.getLeadTechnologist().getName(),
                competition.getAssessorAcceptsDate(),
                competition.getAssessorDeadlineDate(),
                assessments);

        return serviceSuccess(assessorCompetitionDashboardResource);
    }
}