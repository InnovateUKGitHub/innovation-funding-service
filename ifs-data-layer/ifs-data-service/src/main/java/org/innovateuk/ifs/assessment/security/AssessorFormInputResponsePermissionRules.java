package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponsesResource;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

/**
 * Provides the permissions around CRUD operations for {@link org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse} resources.
 */
@Component
@PermissionRules
public class AssessorFormInputResponsePermissionRules extends BasePermissionRules {

    @PermissionRule(value = "UPDATE", description = "Only Assessors can update Assessor Form Input Responses")
    public boolean userCanUpdateAssessorFormInputResponses(AssessorFormInputResponsesResource responses, UserResource user) {
        return responses.getResponses().stream().allMatch(response -> isAssessorForFormInputResponse(response, user));
    }

    private boolean isAssessorForFormInputResponse(AssessorFormInputResponseResource response, UserResource user) {
        Assessment assessment = assessmentRepository.findOne(response.getAssessment());
        Long assessmentUser = processRoleRepository.findOne(assessment.getParticipant().getId()).getUser().getId();
        return user.getId().equals(assessmentUser);
    }
}
