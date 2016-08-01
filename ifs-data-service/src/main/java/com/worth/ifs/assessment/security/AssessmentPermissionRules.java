package com.worth.ifs.assessment.security;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static com.worth.ifs.security.SecurityRuleUtil.isAssessor;
import static com.worth.ifs.security.SecurityRuleUtil.isCompAdmin;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * Provides the permissions around CRUD operations for {@link com.worth.ifs.assessment.domain.Assessment} resources.
 */
@Component
@PermissionRules
public class AssessmentPermissionRules {

    @PermissionRule(value = "READ", description = "Assessors and competitionAdmins can read Assessments")
    public boolean userCanReadAssessment(final Assessment assessment, final UserResource user) {
        return isCompAdmin(user) || (isAssessor(user) && isOwner(assessment, user));
    }

    @PermissionRule(value = "UPDATE", description = "only owners can update Assessments")
    public boolean userCanUpdateAssessment(final Assessment assessment, final UserResource user) {
        return isOwner(assessment, user);
    }

    public boolean isOwner(final Assessment assessment, final UserResource user) {
        return simpleMap(assessment.getProcessRole().getApplication().getProcessRoles(), ProcessRole::getUser).stream()
                .filter(u -> u != null)
                .map(User::getId)
                .anyMatch(user.getId()::equals);
    }
}
