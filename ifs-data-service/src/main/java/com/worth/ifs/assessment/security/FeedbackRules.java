package com.worth.ifs.assessment.security;


import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.assessment.resource.Feedback;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.worth.ifs.user.resource.UserRoleType.ASSESSOR;
import static com.worth.ifs.util.CollectionFunctions.onlyElement;
import static com.worth.ifs.util.CollectionFunctions.onlyElementOrNull;

@Component
@PermissionRules
public class FeedbackRules {

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @PermissionRule(value = "READ", description = "An Assessor can read their own feedback")
    public boolean assessorCanReadTheirOwnFeedback(Feedback dto, UserResource user) {
        return isOwnerOfFeedbackResponseAndAssessorForApplication(dto, user);
    }

    @PermissionRule(value = "UPDATE", description = "An Assessor can update their own feedback")
    public boolean assessorCanUpdateTheirOwnFeedback(Feedback dto, UserResource user){
        return isOwnerOfFeedbackResponseAndAssessorForApplication(dto, user);
    }

    private boolean isOwnerOfFeedbackResponseAndAssessorForApplication(Feedback dto, UserResource user) {

        //
        // If the user accessing this method is not the owner of the Feedback to be accessed, they cannot access it
        //
        if (!isOwnerOfFeedback(dto, user)) {
            return false;
        }

        //
        // If the user is not currently an Assessor on this Application, they shouldn't be able to access it
        //
        Response response = responseRepository.findOne(dto.getResponseId());

        if (response == null) {
            return false;
        }

        Role assessorRole = onlyElement(roleRepository.findByName(ASSESSOR.getName()));
        ProcessRole assessorProcessRole = onlyElementOrNull(processRoleRepository.findByUserIdAndRoleAndApplicationId(user.getId(), assessorRole, response.getApplication().getId()));
        return assessorProcessRole != null;
    }

    private boolean isOwnerOfFeedback(Feedback dto, UserResource user) {
        return user.getId().equals(dto.getAssessorUserId());
    }
}
