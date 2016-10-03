package com.worth.ifs.assessment.security;

import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.security.BasePermissionRules;
import com.worth.ifs.commons.security.PermissionRule;
import com.worth.ifs.commons.security.PermissionRules;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

/**
 * Provides the permissions around CRUD operations for {@link com.worth.ifs.assessment.domain.Assessment} resources.
 */
@Component
@PermissionRules
public class AssessmentPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ", description = "Assessors can read Assessments")
    public boolean userCanReadAssessment(final AssessmentResource assessment, final UserResource user) {
        return isAssessorForAssessment(assessment, user);
    }

    @PermissionRule(value = "UPDATE", description = "Only owners can update Assessments")
    public boolean userCanUpdateAssessment(final AssessmentResource assessment, final UserResource user) {
        return isAssessorForAssessment(assessment, user);
    }

    private boolean isAssessorForAssessment(final AssessmentResource assessment, final UserResource user) {
        Long assessmentUser = processRoleRepository.findOne(assessment.getProcessRole()).getUser().getId();
        return user.getId().equals(assessmentUser);
    }
}
