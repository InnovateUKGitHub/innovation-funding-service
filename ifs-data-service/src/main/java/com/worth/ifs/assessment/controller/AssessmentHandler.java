package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.constant.AssessmentStatus;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.workflow.domain.ProcessStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * AssessmentHandler is responsible to manage the domain logic around the Assessment's domain range.
 * This avoids code coupling and spread knlowledge and responsability over Assessment's and allows us
 * to have the access to them centered here, preventing any incoherence by multiple ways.
 *
 * So this class is responsible to manage interactions with the AssessmentRepository, being a facade between
 * the outside world and the Assessment's world.
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


    /**
     * Get's all the assessments by competition and assessor.
     * By 'All' is meant all the assessments whose invitation was not rejected.
     * Also, groups the assessments by first having the pending ones and only after the open/active/submitted.
     * @param competitionId
     * @param assessorId
     * @return
     */
    public List<Assessment> getAllByCompetitionAndAssessor(Long competitionId, Long assessorId) {

        List<Assessment> allAssessments = new ArrayList<>();

        /* HQL has no select union this this seems to be the simplest solution */
        //1 pending
        allAssessments.addAll(assessments.findByAssessorAndCompetitionAndStatus(assessorId, competitionId, ProcessStatus.PENDING));
        //2 open
        allAssessments.addAll(assessments.findOpenByAssessorAndCompetition(assessorId, competitionId));
        //3 started
        allAssessments.addAll(assessments.findStartedByAssessorAndCompetition(assessorId, competitionId));

        return allAssessments;
    }

    public Assessment getOneByAssessorAndApplication(Long userId, Long applicationId) {
        return assessments.findOneByAssessorAndApplication(userId, applicationId);
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
