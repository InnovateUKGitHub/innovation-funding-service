package com.worth.ifs.assessment.security;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.security.BasePermissionRules;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

/**
 * Provides the permissions around CRUD operations for {@link com.worth.ifs.assessment.domain.AssessorFormInputResponse} resources.
 */
@Component
@PermissionRules
public class AssessorFormInputResponsePermissionRules extends BasePermissionRules {

    @PermissionRule(value = "UPDATE", description = "Only Assessors can update Assessor Form Input Responses")
    public boolean userCanUpdateAssessorFormInputResponse(AssessorFormInputResponseResource response, UserResource user) {
        return isAssessorForFormInputResponse(response, user);
    }

    private boolean isAssessorForFormInputResponse(AssessorFormInputResponseResource response, UserResource user) {
        Assessment assessment = assessmentRepository.findOne(response.getAssessment());
        Long assessmentUser = processRoleRepository.findOne(assessment.getParticipant().getId()).getUser().getId();
        return user.getId().equals(assessmentUser);
    }
}
