package com.worth.ifs.assessment.transactional;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.domain.CompetitionParticipantRole;
import com.worth.ifs.invite.domain.ParticipantStatus;
import com.worth.ifs.invite.mapper.CompetitionParticipantMapper;
import com.worth.ifs.invite.mapper.CompetitionParticipantRoleMapper;
import com.worth.ifs.invite.mapper.ParticipantStatusMapper;
import com.worth.ifs.invite.repository.CompetitionParticipantRepository;
import com.worth.ifs.invite.resource.CompetitionParticipantResource;
import com.worth.ifs.invite.resource.CompetitionParticipantRoleResource;
import com.worth.ifs.invite.resource.ParticipantStatusResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.assessment.resource.AssessmentStates.*;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.partitioningBy;

/**
 * Service for managing {@link com.worth.ifs.invite.domain.CompetitionParticipant}s.
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
            List<Assessment> assessments = assessmentRepository.findByParticipantUserIdAndParticipantApplicationCompetitionId(competitionParticipant.getUserId(),competitionParticipant.getCompetitionId());
            competitionParticipant.setSubmittedAssessments(getAssessmentsSubmittedForCompetitionCount(assessments));
            competitionParticipant.setTotalAssessments(getTotalAssessmentsAcceptedForCompetitionCount(assessments));
        });
    }

    private Long getAssessmentsSubmittedForCompetitionCount(List<Assessment> assessments) {
        return assessments.stream().filter(assessment -> assessment.getActivityState().equals(SUBMITTED)).count();
    }

    private Long getTotalAssessmentsAcceptedForCompetitionCount(List<Assessment> assessments) {
        List<AssessmentStates> allowedAssessmentStates = asList(ACCEPTED, OPEN, READY_TO_SUBMIT, SUBMITTED);
        return assessments.stream().filter(assessment -> allowedAssessmentStates.contains(assessment.getActivityState())).count();
    }
}
