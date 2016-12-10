package org.innovateuk.ifs.assessment.transactional;

import com.google.common.collect.Sets;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.mapper.CompetitionParticipantMapper;
import org.innovateuk.ifs.invite.mapper.CompetitionParticipantRoleMapper;
import org.innovateuk.ifs.invite.mapper.ParticipantStatusMapper;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantRoleResource;
import org.innovateuk.ifs.invite.resource.ParticipantStatusResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.partitioningBy;

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
    private ParticipantStatusMapper participantStatusMapper;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Override
    public ServiceResult<List<CompetitionParticipantResource>> getCompetitionParticipants(@P("user") Long userId,
                                                                                          @P("role") CompetitionParticipantRoleResource roleResource,
                                                                                          @P("status") ParticipantStatusResource statusResource) {

        CompetitionParticipantRole role = competitionParticipantRoleMapper.mapToDomain(roleResource);
        ParticipantStatus status = participantStatusMapper.mapToDomain(statusResource);
        List<CompetitionParticipantResource> competitionParticipants = simpleMap(competitionParticipantRepository.getByUserIdAndRoleAndStatus(userId, role, status), compParticipantMapper::mapToResource);
        determineStatusOfCompetitionAssessments(competitionParticipants);
        return serviceSuccess(competitionParticipants);
    }

    private void determineStatusOfCompetitionAssessments(List<CompetitionParticipantResource> competitionParticipants) {
        competitionParticipants.forEach( competitionParticipant -> {
            List<Assessment> assessments = assessmentRepository.findByParticipantUserIdAndParticipantApplicationCompetitionIdOrderByActivityStateStateAscIdAsc(competitionParticipant.getUserId(),competitionParticipant.getCompetitionId());
            competitionParticipant.setSubmittedAssessments(getAssessmentsSubmittedForCompetitionCount(assessments));
            competitionParticipant.setTotalAssessments(getTotalAssessmentsAcceptedForCompetitionCount(assessments));
        });
    }

    private Long getAssessmentsSubmittedForCompetitionCount(List<Assessment> assessments) {
        return assessments.stream().filter(assessment -> assessment.getActivityState().equals(SUBMITTED)).count();
    }

    private Long getTotalAssessmentsAcceptedForCompetitionCount(List<Assessment> assessments) {
        Set<AssessmentStates> allowedAssessmentStates = Sets.newHashSet(ACCEPTED, OPEN, READY_TO_SUBMIT, SUBMITTED);
        return assessments.stream().filter(assessment -> allowedAssessmentStates.contains(assessment.getActivityState())).count();
    }
}
