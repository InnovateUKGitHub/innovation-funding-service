package org.innovateuk.ifs.assessment.dashboard.transactional;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.assessment.resource.AssessmentTotalScoreResource;
import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
@Transactional(readOnly = true)
public class ApplicationAssessmentServiceImpl implements ApplicationAssessmentService {

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Override
    public ServiceResult<List<ApplicationAssessmentResource>> getApplicationAssessmentResource(long applicationId) {
        Set<AssessmentState> allowedStates = EnumSet.of(SUBMITTED);

        List<Assessment> assessments = assessmentRepository.findByTargetId(applicationId);

        return serviceSuccess(assessments.stream()
                .map(this::mapToResource)
                .filter(assessment -> allowedStates.contains(assessment.getState()))
                .sorted()
                .collect(toList()));
    }

    @Override
    public ServiceResult<List<ApplicationAssessmentResource>> getApplicationAssessmentResource(long userId, long competitionId) {
        Set<AssessmentState> allowedStates = EnumSet.of(PENDING, OPEN, ACCEPTED, READY_TO_SUBMIT, SUBMITTED);

        List<Assessment> assessments = assessmentRepository.findByParticipantUserIdAndTargetCompetitionId(userId, competitionId);

        return serviceSuccess(assessments.stream()
                .map(this::mapToResource)
                .filter(assessment -> allowedStates.contains(assessment.getState()))
                .sorted()
                .collect(toList()));
    }

    @Override
    public ServiceResult<List<ApplicationAssessmentResource>> getApplicationAssessmentResource(long userId, long competitionId, long assessmentPeriodId) {
        Set<AssessmentState> allowedStates = EnumSet.of(PENDING, OPEN, ACCEPTED, READY_TO_SUBMIT, SUBMITTED);

        List<Assessment> assessments = assessmentRepository.findByTargetAssessmentPeriodIdAndAndActivityStateIn(assessmentPeriodId, allowedStates);

        return serviceSuccess(assessments.stream()
                .filter(assessment -> assessment.getParticipant().getUser().getId() == userId &&
                        assessment.getTarget().getCompetition().getId() == competitionId)
                .map(this::mapToResource)
                .sorted()
                .collect(toList()));
    }

    private ApplicationAssessmentResource mapToResource(Assessment assessment) {
        Optional<Organisation> leadOrganisation = organisationRepository.findById(assessment.getTarget().getLeadOrganisationId());
        AssessmentTotalScoreResource assessmentTotalScore = assessmentRepository.getTotalScore(assessment.getId());

        return new ApplicationAssessmentResource(
                assessment.getTarget().getId(),
                assessment.getId(),
                assessment.getTarget().getName(),
                leadOrganisation.get().getName(),
                assessment.getProcessState(),
                getOverallScore(assessment, assessmentTotalScore),
                getTotalScoreGiven(assessment, assessmentTotalScore),
                getRecommended(assessment));
    }

    private int getOverallScore(Assessment assessment, AssessmentTotalScoreResource assessmentTotalScore) {
        switch (assessment.getProcessState()) {
            case READY_TO_SUBMIT:
            case SUBMITTED:
                return assessmentTotalScore.getTotalScorePercentage();
            default:
                return 0;
        }
    }

    private int getTotalScoreGiven(Assessment assessment, AssessmentTotalScoreResource assessmentTotalScore) {
        switch (assessment.getProcessState()) {
            case READY_TO_SUBMIT:
            case SUBMITTED:
                return assessmentTotalScore.getTotalScoreGiven();
            default:
                return 0;
        }
    }

    private Boolean getRecommended(Assessment assessment) {
        return ofNullable(assessment.getFundingDecision())
                .map(fundingDecision -> fundingDecision.isFundingConfirmation()).orElse(null);
    }
}