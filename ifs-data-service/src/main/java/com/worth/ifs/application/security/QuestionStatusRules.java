package com.worth.ifs.application.security;

import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionRules
public class QuestionStatusRules {

    @Autowired
    ProcessRoleRepository processRoleRepository;

    @PermissionRule(value = "READ", description = "users can only read statuses of questions they are assigned to")
    public boolean userCanReadQuestionStatus(QuestionStatusResource questionStatusResource, UserResource user){
        ProcessRole processRole = processRoleRepository.findByUserIdAndApplicationId(user.getId(),  questionStatusResource.getApplication());
        return processRole!=null;
    }
}
