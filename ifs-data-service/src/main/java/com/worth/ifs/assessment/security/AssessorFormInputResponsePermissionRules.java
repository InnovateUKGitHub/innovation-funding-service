package com.worth.ifs.assessment.security;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.security.BasePermissionRules;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static com.worth.ifs.security.SecurityRuleUtil.isCompAdmin;

/**
 * Provides the permissions around CRUD operations for {@link com.worth.ifs.assessment.domain.AssessorFormInputResponse} resources.
 */
@Component
@PermissionRules
public class AssessorFormInputResponsePermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ", description = "Administrators and Assessors can read Assessor Form Input Responses")
    public boolean userCanReadAssessorFormInputResponse(AssessorFormInputResponseResource response, UserResource user) {
        return isCompAdmin(user) || isAssessorForFormInputResponse(response, user);
    }

    @PermissionRule(value = "UPDATE", description = "Only Assessors can update Assessor Form Input Responses")
    public boolean userCanUpdateAssessorFormInputResponse(AssessorFormInputResponseResource response, UserResource user) {
        return isAssessorForFormInputResponse(response, user);
    }

    private boolean isAssessorForFormInputResponse(AssessorFormInputResponseResource response, UserResource user) {
        Assessment assessment = assessmentRepository.findOne(response.getAssessment());
        Long assessmentUser = processRoleRepository.findOne(assessment.getProcessRole().getId()).getUser().getId();
        return user.getId().equals(assessmentUser);
    }
}
