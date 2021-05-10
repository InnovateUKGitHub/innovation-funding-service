package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.mapper.AssessmentInviteMapper;
import org.innovateuk.ifs.assessment.mapper.AssessmentParticipantMapper;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.CompetitionParticipant;
import org.innovateuk.ifs.competition.mapper.CompetitionParticipantRoleMapper;
import org.innovateuk.ifs.invite.mapper.ParticipantStatusMapper;
import org.innovateuk.ifs.invite.mapper.RejectionReasonMapper;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Service for managing {@link CompetitionParticipant}s.
 */
@Service
@Transactional(readOnly = true)
public class CompetitionParticipantServiceImpl implements CompetitionParticipantService {

    @Autowired
    private AssessmentParticipantRepository assessmentParticipantRepository;

    @Autowired
    private AssessmentParticipantMapper compParticipantMapper;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private AssessmentInviteMapper assessmentInviteMapper;

    @Autowired
    private RejectionReasonMapper rejectionReasonMapper;

    @Autowired
    private CompetitionParticipantRoleMapper competitionParticipantRoleMapper;

    @Autowired
    private ParticipantStatusMapper participantStatusMapper;

    @Override
    public ServiceResult<List<CompetitionParticipantResource>> getCompetitionAssessors(long userId) {

        List<CompetitionParticipantResource> competitionParticipantResources = assessmentParticipantRepository.getByAssessorId(userId).stream()
                .map(compParticipantMapper::mapToResource)
                .filter(participant -> !participant.isRejected() && participant.isUpcomingOrInAssessment())
                .collect(toList());

        competitionParticipantResources.forEach(this::determineStatusOfCompetitionAssessments);

        return serviceSuccess(competitionParticipantResources);
    }

    @Override
    public ServiceResult<List<CompetitionParticipantResource>> getCompetitionAssessmentPeriodByAssessors(long userId) {

        List<CompetitionParticipantResource> competitionParticipantResources = assessmentParticipantRepository.getByAssessorId(userId).stream()
                .flatMap(assessmentParticipant -> assessmentParticipant.getProcess().getAssessmentPeriods().stream()
                        .map(assessmentPeriod -> new CompetitionParticipantResource(assessmentParticipant.getId(), assessmentParticipant.getProcess().getId(),
                                assessmentParticipant.getUser().getId(), assessmentInviteMapper.mapToResource(assessmentParticipant.getInvite()),
                                rejectionReasonMapper.mapToResource(assessmentParticipant.getRejectionReason()), assessmentParticipant.getRejectionReasonComment(),
                                competitionParticipantRoleMapper.mapToResource(assessmentParticipant.getRole()), participantStatusMapper.mapToResource(assessmentParticipant.getStatus()),
                                assessmentParticipant.getProcess().getName(), assessmentParticipant.getProcess().getAssessorAcceptsDate(assessmentPeriod),
                                assessmentParticipant.getProcess().getAssessorDeadlineDate(assessmentPeriod), assessmentParticipant.getProcess().getCompetitionStatus(),
                                assessmentParticipant.getProcess().getAlwaysOpen(), assessmentPeriod.getId(), assessmentParticipant.getProcess().getAssessmentPeriodStatus(assessmentPeriod)))
                )
                .filter(competitionParticipant -> !competitionParticipant.isRejected() && isUpcomingOrInAssessment(competitionParticipant))
                .collect(toList());

        competitionParticipantResources.forEach(this::determineStatusOfCompetitionAssessments);

        return serviceSuccess(competitionParticipantResources);
    }

    private boolean isUpcomingOrInAssessment(CompetitionParticipantResource competitionParticipant) {
        if (competitionParticipant.isCompetitionAlwaysOpen()) {
            return competitionParticipant.isUpcomingOrInAssessmentPeriod();
        } else {
            return competitionParticipant.isUpcomingOrInAssessment();
        }
    }

    private void determineStatusOfCompetitionAssessments(CompetitionParticipantResource competitionParticipant) {
        if (!competitionParticipant.isAccepted() || !isInAssessment(competitionParticipant)) {
            return;
        }

        List<Assessment> assessments = assessmentRepository.findByParticipantUserIdAndTargetCompetitionId(
                competitionParticipant.getUserId(),
                competitionParticipant.getCompetitionId()
        );

        competitionParticipant.setSubmittedAssessments(getAssessmentsSubmittedForCompetitionCount(assessments, competitionParticipant.getAssessmentPeriodId()));
        competitionParticipant.setTotalAssessments(getTotalAssessmentsAcceptedForCompetitionCount(assessments, competitionParticipant.getAssessmentPeriodId()));
        competitionParticipant.setPendingAssessments(getAssessmentsPendingForCompetitionCount(assessments, competitionParticipant.getAssessmentPeriodId()));
    }

    private boolean isInAssessment(CompetitionParticipantResource competitionParticipant) {
        if (competitionParticipant.isCompetitionAlwaysOpen()) {
            return competitionParticipant.isInAssessmentPeriod();
        } else {
            return competitionParticipant.isInAssessment();
        }
    }

    private Long getAssessmentsSubmittedForCompetitionCount(List<Assessment> assessments, Long assessmentPeriodId) {
        return assessments.stream()
                .filter(assessment -> assessment.getProcessState().equals(SUBMITTED)
                        && filterByAssessmentPeriod(assessmentPeriodId, assessment))
                .count();
    }

    private boolean filterByAssessmentPeriod(Long assessmentPeriodId, Assessment assessment) {
        if (assessmentPeriodId != null) {
            return assessment.getTarget().getAssessmentPeriod().getId() == assessmentPeriodId;
        } else {
            return true;
        }
    }

    private Long getTotalAssessmentsAcceptedForCompetitionCount(List<Assessment> assessments, Long assessmentPeriodId) {
        Set<AssessmentState> allowedAssessmentStates = EnumSet.of(ACCEPTED, OPEN, READY_TO_SUBMIT, SUBMITTED);
        return assessments.stream()
                .filter(assessment -> allowedAssessmentStates.contains(assessment.getProcessState())
                        && filterByAssessmentPeriod(assessmentPeriodId, assessment))
                .count();
    }

    private Long getAssessmentsPendingForCompetitionCount(List<Assessment> assessments, Long assessmentPeriodId) {
        return assessments.stream()
                .filter(assessment -> assessment.getProcessState().equals(PENDING)
                        && filterByAssessmentPeriod(assessmentPeriodId, assessment))
                .count();
    }
}
