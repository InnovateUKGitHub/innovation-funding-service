package com.worth.ifs.assessment.security;

import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.security.BasePermissionRules;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static com.worth.ifs.security.SecurityRuleUtil.isCompAdmin;

/**
 * Provides the permissions around CRUD operations for {@link com.worth.ifs.assessment.domain.Assessment} resources.
 */
@Component
@PermissionRules
public class AssessmentPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ", description = "Assessors and competitionAdmins can read Assessments")
    public boolean userCanReadAssessment(final AssessmentResource assessment, final UserResource user) {
        return isCompAdmin(user) || isAssessorForAssessment(assessment, user);
    }

    @PermissionRule(value = "UPDATE", description = "only owners can update Assessments")
    public boolean userCanUpdateAssessment(final AssessmentResource assessment, final UserResource user) {
        return isAssessorForAssessment(assessment, user);
    }

    private boolean isAssessorForAssessment(final AssessmentResource assessment, final UserResource user) {
        Long assessmentUser = processRoleRepository.findOne(assessment.getProcessRole()).getUser().getId();
        return user.getId().equals(assessmentUser);
    }
}
