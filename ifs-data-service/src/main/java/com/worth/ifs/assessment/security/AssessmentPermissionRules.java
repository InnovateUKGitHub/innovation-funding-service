package com.worth.ifs.assessment.security;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.mapper.AssessmentMapper;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.commons.security.PermissionRule;
import com.worth.ifs.commons.security.PermissionRules;
import com.worth.ifs.security.BasePermissionRules;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.workflow.domain.ActivityState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.worth.ifs.assessment.resource.AssessmentStates.*;

/**
 * Provides the permissions around CRUD operations for {@link com.worth.ifs.assessment.domain.Assessment} resources.
 */
@Component
@PermissionRules
public class AssessmentPermissionRules extends BasePermissionRules {
    @Autowired
    AssessmentMapper mapper;

    @Autowired
    AssessmentRepository assessmentRepository;

    @PermissionRule(value = "READ", description = "Assessors can read all Assessments, except those rejected")
    public boolean userCanReadAssessment(AssessmentResource assessment, UserResource user) {
        Stream<AssessmentStates> allowedStates = Stream.of(PENDING, OPEN, READY_TO_SUBMIT, SUBMITTED);
        return isAssessorForAssessment(assessment, user, allowedStates);
    }

    @PermissionRule(value = "READ_NON_DASHBOARD", description = "Rule for reading assessments not on the competition dashboard")
    public boolean userCanReadAssessmentNonDashboard(AssessmentResource assessment, final UserResource user) {
        Stream<AssessmentStates> allowedStates = Stream.of(PENDING, OPEN, READY_TO_SUBMIT);
        return isAssessorForAssessment(assessment, user, allowedStates);
    }

    @PermissionRule(value = "UPDATE", description = "Only owners can update Assessments")
    public boolean userCanUpdateAssessment(AssessmentResource assessment, UserResource user) {
        Stream<AssessmentStates> allowedStates = Stream.of(PENDING, OPEN, READY_TO_SUBMIT, SUBMITTED);
        return isAssessorForAssessment(assessment, user, allowedStates);
    }

    private boolean isAssessorForAssessment(AssessmentResource assessment, UserResource user, Stream<AssessmentStates> allowedStates) {
        Long assessmentUser = processRoleRepository.findOne(assessment.getProcessRole()).getUser().getId();
        return user.getId().equals(assessmentUser) && assessmentHasViewableState(assessment, allowedStates);
    }

    private boolean assessmentHasViewableState(AssessmentResource assessmentResource, Stream<AssessmentStates> allowedStates) {
        Assessment assessment = assessmentRepository.findOne(assessmentResource.getId());
        return allowedStates.anyMatch(state -> state.equals(assessment.getActivityState()));
    }
}
