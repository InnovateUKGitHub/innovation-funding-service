package org.innovateuk.ifs.assessment.dashboard.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.dashboard.transactional.AssessorCompetitionDashboardService;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentTotalScoreResource;
import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class AssessorCompetitionDashboardServiceImpl extends BaseTransactionalService implements AssessorCompetitionDashboardService {

    @Autowired
    AssessmentRepository assessmentRepository;

    @Override
    public ServiceResult<ApplicationAssessmentResource> getAssessorCompetitionDashboard(long userId, long competitionId) {
        List<Assessment> assessments = assessmentRepository.findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateAscIdAsc(userId, competitionId);

        List<ApplicationAssessmentResource> applicationAssessmentResources = assessments.stream()
                .map(assessment -> mapToResource(assessment))
                .collect(toList());


        return serviceSuccess(applicationAssessmentResources);
    }

    private ApplicationAssessmentResource mapToResource(Assessment assessment) {

        Application application = assessment.getTarget();
        Optional<Organisation> leadOrganisation = organisationRepository.findById(assessment.getTarget().getLeadOrganisationId());
        AssessmentTotalScoreResource score = assessmentRepository.getTotalScore(assessment.getId());

        return new ApplicationAssessmentResource(
                application.getId(),
                assessment.getId(),
                application.getCompetition().getName(),
                leadOrganisation.get().getName(),
                assessment.getProcessState(),
                assessment.,
                getRecommended(assessment)
                );
    }

    private Boolean getRecommended(Assessment assessment) {
        return ofNullable(assessment.getFundingDecision())
                .map(fundingDecisionResource -> fundingDecisionResource.getFundingConfirmation()).orElse(null);
    }

}
