package org.innovateuk.ifs.assessment.dashboard.transactional;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentTotalScoreResource;
import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.transactional.RootTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class ApplicationAssessmentServiceImpl extends RootTransactionalService implements ApplicationAssessmentService {

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Override
    public ServiceResult<List<ApplicationAssessmentResource>> getApplicationAssessmentResource(long userId, long competitionId) {
        List<Assessment> assessments = assessmentRepository.findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateAscIdAsc(userId, competitionId);

        return serviceSuccess(assessments.stream()
                .map(assessment -> mapToResource(assessment))
                .collect(toList()));
    }

    private ApplicationAssessmentResource mapToResource(Assessment assessment) {
        Optional<Organisation> leadOrganisation = organisationRepository.findById(assessment.getTarget().getLeadOrganisationId());

        return new ApplicationAssessmentResource(
                assessment.getTarget().getId(),
                assessment.getId(),
                assessment.getTarget().getCompetition().getName(),
                leadOrganisation.get().getName(),
                assessment.getProcessState(),
                getOverallScore(assessment),
                getRecommended(assessment));
    }

    private int getOverallScore(Assessment assessment) {
        switch (assessment.getProcessState()) {
            case READY_TO_SUBMIT:
            case SUBMITTED:
                AssessmentTotalScoreResource assessmentTotalScore = assessmentRepository.getTotalScore(assessment.getId());
                return assessmentTotalScore.getTotalScorePercentage();
            default:
                return 0;
        }
    }

    private Boolean getRecommended(Assessment assessment) {
        return ofNullable(assessment.getFundingDecision())
                .map(fundingDecision -> fundingDecision.isFundingConfirmation()).orElse(null);
    }
}