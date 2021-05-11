package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.assessment.mapper.AssessmentInviteMapper;
import org.innovateuk.ifs.assessment.mapper.AssessmentParticipantMapper;
import org.innovateuk.ifs.assessment.period.domain.AssessmentPeriod;
import org.innovateuk.ifs.assessment.period.mapper.AssessmentPeriodMapper;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.CompetitionParticipant;
import org.innovateuk.ifs.competition.mapper.CompetitionParticipantRoleMapper;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.invite.mapper.ParticipantStatusMapper;
import org.innovateuk.ifs.invite.mapper.RejectionReasonMapper;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Stream;

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

    @Autowired
    private AssessmentPeriodMapper assessmentPeriodMapper;

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
    public ServiceResult<List<CompetitionParticipantResource>> getCompetitionAssessorsWithAssessmentPeriod(long userId) {

        List<CompetitionParticipantResource> competitionParticipantResources = assessmentParticipantRepository.getByAssessorId(userId).stream()
                .flatMap(assessmentParticipant -> assessmentParticipant.getProcess().getAssessmentPeriods().stream()
                        .map(assessmentPeriod -> buildCompetitionParticipantResource(assessmentParticipant, assessmentPeriod))
                )
                .filter(competitionParticipant -> !competitionParticipant.isRejected() && isUpcomingOrInAssessment(competitionParticipant))
                .collect(toList());

        competitionParticipantResources.forEach(this::determineStatusOfCompetitionAssessments);

        return serviceSuccess(competitionParticipantResources);
    }

    private CompetitionParticipantResource buildCompetitionParticipantResource(AssessmentParticipant assessmentParticipant, AssessmentPeriod assessmentPeriod) {
        return new CompetitionParticipantResource(assessmentParticipant.getId(), assessmentParticipant.getProcess().getId(),
                assessmentParticipant.getUser().getId(), assessmentInviteMapper.mapToResource(assessmentParticipant.getInvite()),
                rejectionReasonMapper.mapToResource(assessmentParticipant.getRejectionReason()), assessmentParticipant.getRejectionReasonComment(),
                competitionParticipantRoleMapper.mapToResource(assessmentParticipant.getRole()), participantStatusMapper.mapToResource(assessmentParticipant.getStatus()),
                assessmentParticipant.getProcess().getName(), assessmentParticipant.getProcess().getAssessorAcceptsDate(assessmentPeriod),
                assessmentParticipant.getProcess().getAssessorDeadlineDate(assessmentPeriod), assessmentParticipant.getProcess().getCompetitionStatus(),
                assessmentParticipant.getProcess().getAlwaysOpen(), assessmentPeriodMapper.mapToResource(assessmentPeriod));
    }

    private boolean isUpcomingOrInAssessment(CompetitionParticipantResource competitionParticipant) {
        return competitionParticipant.getAssessmentPeriod().isInAssessment()
                || !competitionParticipant.getAssessmentPeriod().isAssessmentClosed();
    }

    private void determineStatusOfCompetitionAssessments(CompetitionParticipantResource competitionParticipant) {
        if (!competitionParticipant.isAccepted() || !isInAssessment(competitionParticipant)) {
            return;
        }

        List<Assessment> assessments = assessmentRepository.findByParticipantUserIdAndTargetCompetitionId(
                competitionParticipant.getUserId(),
                competitionParticipant.getCompetitionId()
        );

        competitionParticipant.setSubmittedAssessments(getAssessmentsSubmittedForCompetitionCount(assessments, competitionParticipant.getAssessmentPeriod()));
        competitionParticipant.setTotalAssessments(getTotalAssessmentsAcceptedForCompetitionCount(assessments, competitionParticipant.getAssessmentPeriod()));
        competitionParticipant.setPendingAssessments(getAssessmentsPendingForCompetitionCount(assessments, competitionParticipant.getAssessmentPeriod()));
    }

    private boolean isInAssessment(CompetitionParticipantResource competitionParticipant) {
        return competitionParticipant.getAssessmentPeriod() != null
                && competitionParticipant.getAssessmentPeriod().isInAssessment();
    }

    private Long getAssessmentsSubmittedForCompetitionCount(List<Assessment> assessments, AssessmentPeriodResource assessmentPeriod) {
        Stream<Assessment> assessmentStream =  assessments.stream()
                .filter(assessment -> assessment.getProcessState().equals(SUBMITTED));

        assessmentStream = filterByAssessmentPeriod(assessmentStream, assessmentPeriod);

        return assessmentStream.count();
    }

    private Stream<Assessment> filterByAssessmentPeriod(Stream<Assessment> assessmentStream, AssessmentPeriodResource assessmentPeriod) {
        return assessmentStream
                .filter(assessment -> assessment.getTarget().getAssessmentPeriod().getId() == assessmentPeriod.getId());
    }

    private Long getTotalAssessmentsAcceptedForCompetitionCount(List<Assessment> assessments, AssessmentPeriodResource assessmentPeriod) {
        Set<AssessmentState> allowedAssessmentStates = EnumSet.of(ACCEPTED, OPEN, READY_TO_SUBMIT, SUBMITTED);
        Stream<Assessment> assessmentStream = assessments.stream()
                .filter(assessment -> allowedAssessmentStates.contains(assessment.getProcessState()));

        assessmentStream = filterByAssessmentPeriod(assessmentStream, assessmentPeriod);

        return assessmentStream.count();
    }

    private Long getAssessmentsPendingForCompetitionCount(List<Assessment> assessments, AssessmentPeriodResource assessmentPeriod) {
        Stream<Assessment> assessmentStream = assessments.stream()
                .filter(assessment -> assessment.getProcessState().equals(PENDING));

        assessmentStream = filterByAssessmentPeriod(assessmentStream, assessmentPeriod);

        return assessmentStream.count();
    }
}
