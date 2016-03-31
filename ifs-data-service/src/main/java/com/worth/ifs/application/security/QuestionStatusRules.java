package com.worth.ifs.application.security;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;

import org.springframework.stereotype.Component;

@Component
@PermissionRules
public class QuestionStatusRules {

    @PermissionRule(value = "READ", description = "users can only read statuses of questions they are assigned to")
    public boolean userCanReadQuestionStatus(QuestionStatusResource questionStatusResource, User user){
        return user.getProcessRoles().stream()
                .map(ProcessRole::getApplication)
                .map(Application::getId)
                .anyMatch(questionStatusResource.getApplication()::equals);
    }
}
