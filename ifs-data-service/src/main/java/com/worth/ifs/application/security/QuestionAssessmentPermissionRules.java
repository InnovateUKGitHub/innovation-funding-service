package com.worth.ifs.application.security;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.commons.security.PermissionRule;
import com.worth.ifs.commons.security.PermissionRules;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static com.worth.ifs.security.SecurityRuleUtil.isAnonymous;

@Component
@PermissionRules
public class QuestionAssessmentPermissionRules {
    @PermissionRule(value = "READ", description = "logged in users can see all questions")
    public boolean loggedInUsersCanSeeAllQuestions(QuestionResource questionResource, UserResource user){
        return !isAnonymous(user);
    }

    @PermissionRule(value = "READ", description = "logged in users can see all questions")
    public boolean loggedInUsersCanSeeAllQuestions(Question question, UserResource user){
        return !isAnonymous(user);
    }

    @PermissionRule(value = "UPDATE", description = "no users can currently update questions")
    public boolean noUserCanUpdateAny(QuestionResource questionResource, UserResource user){
        return false;
    }

}
