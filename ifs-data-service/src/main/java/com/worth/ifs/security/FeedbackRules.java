package com.worth.ifs.security;


import com.worth.ifs.assessment.dto.Feedback;
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

    @PermissionRule("READ")
    public boolean hasPermission1(Feedback dto, User user) {
        return isAssessorForApplication(dto, user);
    }

    @PermissionRule("UPDATE")
    public boolean hasPermission2(Feedback dto, User user){
        return isAssessorForApplication(dto, user);
    }

    private boolean isAssessorForApplication(Feedback dto, User user) {
        ProcessRole processRole = processRoleRepository.findOne(dto.getAssessorProcessRoleId());
        return processRole.getRole().getName().equals(UserRoleType.ASSESSOR.getName()) &&
                processRole.getUser().equals(user);
    }
}
