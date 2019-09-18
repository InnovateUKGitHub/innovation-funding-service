package org.innovateuk.ifs.assessment.security;

import com.google.common.collect.Streams;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.assessment.resource.AssessmentSubmissionsResource;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.review.repository.ReviewRepository;
import org.innovateuk.ifs.review.resource.ReviewState;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static org.innovateuk.ifs.assessment.resource.AssessmentState.*;

/**
 * Provides the permissions around CRUD operations for {@link org.innovateuk.ifs.assessment.domain.Assessment} resources.
 */
@Component
@PermissionRules
public class AssessmentPermissionRules extends BasePermissionRules {
    @Autowired
    private ReviewRepository reviewRepository;

    @PermissionRule(value = "READ_DASHBOARD", description = "Assessors can view all Assessments on the competition " +
            "dashboard, except those rejected or withdrawn")
    public boolean userCanReadAssessmentOnDashboard(AssessmentResource assessment, UserResource user) {
        Set<AssessmentState> allowedStates = EnumSet.of(PENDING, ACCEPTED, OPEN, READY_TO_SUBMIT, SUBMITTED);
        return isAssessorForAssessment(assessment, user, allowedStates);
    }

    @PermissionRule(value = "READ", description = "Assessors can directly read Assessments that are accepted, open or ready to submit")
    public boolean userCanReadAssessment(AssessmentResource assessment, UserResource user) {
        Set<AssessmentState> allowedStates = EnumSet.of(ACCEPTED, OPEN, READY_TO_SUBMIT);
        return isAssessorForAssessment(assessment, user, allowedStates);
    }

    @PermissionRule(value = "READ", description = "Assessors can directly read Assessments that are in panel")
    public boolean userIsPanelAssessor(AssessmentResource assessment, UserResource user) {
        Long assessmentUserId = processRoleRepository.findById(assessment.getProcessRole()).get().getUser().getId();
        return user.getId().equals(assessmentUserId) &&
                reviewRepository.existsByParticipantUserIdAndTargetIdAndActivityStateNot(assessmentUserId, assessment.getApplication(), ReviewState.WITHDRAWN);
    }

    @PermissionRule(value = "READ_SCORE", description = "Assessors can read the score of Assessments except those pending or rejected")
    public boolean userCanReadAssessmentScore(AssessmentResource assessment, UserResource user) {
        Set<AssessmentState> allowedStates = EnumSet.of(ACCEPTED, OPEN, READY_TO_SUBMIT, SUBMITTED);
        return isAssessorForAssessment(assessment, user, allowedStates);
    }

    @PermissionRule(value = "READ_TO_ASSIGN", description = "Assessors can read pending assessments to decide to " +
            "either to accept or reject")
    public boolean userCanReadToAssign(AssessmentResource assessment, UserResource user) {
        Set<AssessmentState> allowedStates = Collections.singleton(PENDING);
        return isAssessorForAssessment(assessment, user, allowedStates);
    }

    @PermissionRule(value = "READ_TO_REJECT", description = "Assessors can reject assessments that are pending, accepted, open or ready to submit")
    public boolean userCanReadToReject(AssessmentResource assessment, UserResource user) {
        Set<AssessmentState> allowedStates = EnumSet.of(PENDING, ACCEPTED, OPEN, READY_TO_SUBMIT);
        return isAssessorForAssessment(assessment, user, allowedStates);
    }

    @PermissionRule(value = "UPDATE", description = "Only owners can update Assessments")
    public boolean userCanUpdateAssessment(AssessmentResource assessment, UserResource user) {
        Set<AssessmentState> allowedStates = EnumSet.of(CREATED, PENDING, ACCEPTED, OPEN, READY_TO_SUBMIT);
        return isAssessorForAssessment(assessment, user, allowedStates);
    }

    @PermissionRule(value = "SUBMIT", description = "Only owners can submit Assessments")
    public boolean userCanSubmitAssessments(AssessmentSubmissionsResource submissions, UserResource user) {
        return Streams.stream(assessmentRepository.findAllById(submissions.getAssessmentIds()))
                .allMatch(assessment -> assessment.getParticipant().getUser().getId().equals(user.getId()));
    }

    private boolean isAssessorForAssessment(AssessmentResource assessment, UserResource user, Set<AssessmentState> allowedStates) {
        return isAssessorForAssessment(assessment, user) && assessmentIsInState(assessment, allowedStates);
    }

    private boolean isAssessorForAssessment(AssessmentResource assessment, UserResource user) {
        Long assessmentUser = processRoleRepository.findById(assessment.getProcessRole()).get().getUser().getId();
        return user.getId().equals(assessmentUser);
    }

    private boolean assessmentIsInState(AssessmentResource assessmentResource, Set<AssessmentState> allowedStates) {
        Assessment assessment = assessmentRepository.findById(assessmentResource.getId()).get();
        return allowedStates.contains(assessment.getProcessState());
    }
}
