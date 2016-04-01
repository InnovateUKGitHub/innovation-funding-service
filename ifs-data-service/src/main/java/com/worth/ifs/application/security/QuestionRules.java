package com.worth.ifs.application.security;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;

import org.springframework.stereotype.Component;

@Component
@PermissionRules
public class QuestionRules {
    @PermissionRule(value = "READ", description = "")
    public boolean userCanSeeQuestionTheyAreConnectedTo(QuestionResource questionResource, User user){
        return user.getProcessRoles().stream()
                .map(ProcessRole::getApplication)
                .map(Application::getCompetition)
                .map(Competition::getId)
                .anyMatch(questionResource.getCompetition()::equals);
    }

}
