package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.invite.mapper.CompetitionParticipantMapper;
import org.innovateuk.ifs.invite.mapper.CompetitionParticipantRoleMapper;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantRoleResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Service for managing {@link org.innovateuk.ifs.invite.domain.CompetitionParticipant}s.
 */
@Service
public class CompetitionParticipantServiceImpl implements CompetitionParticipantService {

    @Autowired
    private CompetitionParticipantRepository competitionParticipantRepository;

    @Autowired
    private CompetitionParticipantMapper compParticipantMapper;

    @Autowired
    private CompetitionParticipantRoleMapper competitionParticipantRoleMapper;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Override
    public ServiceResult<List<CompetitionParticipantResource>> getCompetitionParticipants(Long userId,
                                                                                          CompetitionParticipantRoleResource roleResource) {

        CompetitionParticipantRole role = competitionParticipantRoleMapper.mapToDomain(roleResource);

        List<CompetitionParticipantResource> competitionParticipantResources = competitionParticipantRepository.getByUserIdAndRole(userId, role).stream()
                .map(compParticipantMapper::mapToResource)
                .filter(participant -> !participant.isRejected() && participant.isUpcomingOrInAssessment())
                .collect(toList());

        competitionParticipantResources.forEach(this::determineStatusOfCompetitionAssessments);

        return serviceSuccess(competitionParticipantResources);
    }

    private void determineStatusOfCompetitionAssessments(CompetitionParticipantResource competitionParticipant) {
        if (!competitionParticipant.isAccepted() || !competitionParticipant.isInAssessment()) {
            return;
        }

        List<Assessment> assessments = assessmentRepository.findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateStateAscIdAsc(
                competitionParticipant.getUserId(),
                competitionParticipant.getCompetitionId()
        );

        competitionParticipant.setSubmittedAssessments(getAssessmentsSubmittedForCompetitionCount(assessments));
        competitionParticipant.setTotalAssessments(getTotalAssessmentsAcceptedForCompetitionCount(assessments));
        competitionParticipant.setPendingAssessments(getAssessmentsPendingForCompetitionCount(assessments));
    }

    private Long getAssessmentsSubmittedForCompetitionCount(List<Assessment> assessments) {
        return assessments.stream().filter(assessment -> assessment.getActivityState().equals(SUBMITTED)).count();
    }

    private Long getTotalAssessmentsAcceptedForCompetitionCount(List<Assessment> assessments) {
        Set<AssessmentStates> allowedAssessmentStates = EnumSet.of(ACCEPTED, OPEN, READY_TO_SUBMIT, SUBMITTED);
        return assessments.stream().filter(assessment -> allowedAssessmentStates.contains(assessment.getActivityState())).count();
    }

    private Long getAssessmentsPendingForCompetitionCount(List<Assessment> assessments) {
        return assessments.stream().filter(assessment -> assessment.getActivityState().equals(PENDING)).count();
    }
}
