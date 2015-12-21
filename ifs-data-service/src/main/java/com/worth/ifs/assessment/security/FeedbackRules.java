package com.worth.ifs.assessment.security;


import com.worth.ifs.assessment.dto.Feedback;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionRules
public class FeedbackRules {

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @PermissionRule(value = "READ", description = "An Assessor can read their own feedback")
    public boolean assessorCanReadTheirOwnFeedback(Feedback dto, User user) {
        return isAssessorForApplication(dto, user);
    }

    @PermissionRule(value = "UPDATE", description = "An Assessor can update their own feedback")
    public boolean assessorCanUpdateTheirOwnFeedback(Feedback dto, User user){
        return isAssessorForApplication(dto, user);
    }

    private boolean isAssessorForApplication(Feedback dto, User user) {
        ProcessRole processRole = processRoleRepository.findOne(dto.getAssessorProcessRoleId());
        return processRole.getRole().getName().equals(UserRoleType.ASSESSOR.getName()) &&
                processRole.getUser() != null &&
                user != null &&
                processRole.getUser().getId().equals(user.getId());
    }
}
