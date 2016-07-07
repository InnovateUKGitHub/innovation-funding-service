package com.worth.ifs.assessment.security;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.worth.ifs.security.SecurityRuleUtil.isCompAdmin;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * Provides the permissions around CRUD operations for {@link com.worth.ifs.assessment.domain.Assessment} resources.
 */
@Component
@PermissionRules
public class AssessmentPermissionRules {
    @Autowired
    private AssessmentRepository assessmentRepository;

    @PermissionRule(value = "READ", description = "users and competitionAdmins can read Assessments")
    public static boolean userCanReadAssessment(final Assessment assessment, final UserResource user) {
        return isCompAdmin(user) || isOwner(assessment, user);
    }

    @PermissionRule(value = "UPDATE", description = "only owners can update Assessments")
    public static boolean userCanUpdateAssessment(final Assessment assessment, final UserResource user) {
        return isOwner(assessment, user);
    }

    static boolean isOwner(final Assessment assessment, final UserResource user) {
        return simpleMap(assessment.getProcessRole().getApplication().getProcessRoles(), ProcessRole::getUser).stream()
                .filter(u -> u != null)
                .map(User::getId)
                .anyMatch(user.getId()::equals);
    }
}
