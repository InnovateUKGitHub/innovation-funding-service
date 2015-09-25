package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.constant.AssessmentStatus;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Created by nunoalexandre on 16/09/15.
 */
@Component
public class AssessmentHandler {

    @Autowired
    private AssessmentRepository assessments;

    public AssessmentHandler(){}

    public void save(Assessment a) {
        assessments.save(a);
    }

    public Assessment saveAndGet(Assessment a) {
        return assessments.save(a);
    }

    public Assessment getOne(Long id) {
        return assessments.findById(id);
    }

    public Set<Assessment> getAllByCompetitionAndUser(Long competitionId, Long userId) {
        return assessments.findByAssessorAndCompetition(userId, competitionId);
    }

    public Assessment getOneByAssessorAndApplication(Long userId, Long applicationId) {
        return assessments.findOneByAssessorAndApplication(userId, applicationId);
    }

    public Set<Assessment> getAllByAssessor(Long assessorId) {
            return assessments.findByAssessorId(assessorId);
    }
    public Integer getTotalSubmittedAssessmentsByCompetition(Long competitionId, Long userId) {
        return assessments.findNumberOfSubmittedAssessmentsByCompetition(userId, competitionId);
    }
    public Integer getTotalAssignedAssessmentsByCompetition(Long competitionId, Long userId) {
        // By 'assigned' is meant an assessment process not rejected
        return assessments.findNumberOfAssignedAssessmentsByCompetition(userId, competitionId);
    }

    /**
     * Method responsible to receive a decision response from the assessor with ID assessorId to the invitation to
     * assess the application with ID applicationId.
     *
     * @param assessorId
     * @param applicationId
     * @param decision
     * @param observations
     * @return false if its not a valid response:
     *      - there is no ASSESSMENT_INVITATION connecting assessorId and applicationId
     *      - if the decision was already made before, ie, if the status is not PENDING
     * @return true othwerise
     */
    public Boolean respondToAssessmentInvitation(Long assessorId, Long applicationId, Boolean decision, String decisionReason, String observations) {

        // Gets assessment
        Assessment assessment = getOneByAssessorAndApplication(assessorId, applicationId);

        // ensures the acceptance only happens if its valid to accept the invitation
        boolean isValid = assessment != null && assessment.getAssessmentStatus().equals(AssessmentStatus.PENDING);

        if ( isValid ) {
            assessment.respondToAssessmentInvitation(decision, decisionReason, observations);
            assessments.save(assessment);
        }

        return isValid;
    }


    public Boolean submitAssessments(Long assessorId, Set<Long> assessmentsToSubmit) {

        for ( Long assessmentId : assessmentsToSubmit ) {
            Assessment assessment = assessments.findById(assessmentId);
            if ( assessmentIsValidToSubmit( assessment ) ) {
                assessment.submit();
                assessments.save(assessment);
            }
        }
        return new Boolean(true);
    }


    public Boolean assessmentIsValidToSubmit( Assessment assessment ) {
        return assessment != null && assessment.hasAssessmentStarted() &&  ! assessment.isSubmitted();
    }


}
