package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.security.SecurityRuleUtil.isAnonymous;

@Component
@PermissionRules
public class QuestionPermissionRules {
    @PermissionRule(value = "READ", description = "Logged in users can see all questions")
    public boolean loggedInUsersCanSeeAllQuestions(QuestionResource questionResource, UserResource user){
        return !isAnonymous(user);
    }

    @PermissionRule(value = "READ", description = "Logged in users can see all questions")
    public boolean loggedInUsersCanSeeAllQuestions(Question question, UserResource user){
        return !isAnonymous(user);
    }

    @PermissionRule(value = "UPDATE", description = "No users can currently update questions")
    public boolean noUserCanUpdateAny(QuestionResource questionResource, UserResource user){
        return false;
    }

    @PermissionRule(value = "ASSESS", description = "Only questions for the assessors can be assessed")
    public boolean onlyAssessableQuestionsCanBeAssessed(QuestionResource questionResource, UserResource user) {
        return !questionResource.getMultipleStatuses();
    }
}
