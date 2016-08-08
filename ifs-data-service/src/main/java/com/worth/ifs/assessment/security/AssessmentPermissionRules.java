package com.worth.ifs.assessment.security;

import com.worth.ifs.assessment.domain.Assessment;
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
public class AssessmentPermissionRules {

    @PermissionRule(value = "READ", description = "Assessors and competitionAdmins can read Assessments")
    public boolean userCanReadAssessment(final Assessment assessment, final UserResource user) {
        return isCompAdmin(user) || isAssessor(assessment, user);
    }

    @PermissionRule(value = "UPDATE", description = "only owners can update Assessments")
    public boolean userCanUpdateAssessment(final Assessment assessment, final UserResource user) {
        return isAssessor(assessment, user);
    }

    public boolean isAssessor(final Assessment assessment, final UserResource user) {
        return assessment.getProcessRole().getUser().getId().equals(user.getId());
    }
}
