package com.worth.ifs.application.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.security.CustomPermissionEvaluator;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static com.worth.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;

public class QuestionPermissionRulesTest extends BasePermissionRulesTest<QuestionPermissionRules> {

    private UserResource anonymousUser;
    private UserResource loggedInUser;
    private QuestionResource questionResource;

    @Override
    protected QuestionPermissionRules supplyPermissionRulesUnderTest() {
        return new QuestionPermissionRules();
    }



    @Before
    public void setup() throws Exception {
        loggedInUser = newUserResource().build();
        questionResource = newQuestionResource().build();
        anonymousUser = (UserResource) ReflectionTestUtils.getField(new CustomPermissionEvaluator(), "ANONYMOUS_USER");
    }


    @Test
    public void testAllUsersCanSeeQuestions() {
        Assert.assertTrue(rules.loggedInUsersCanSeeAllQuestions(questionResource, loggedInUser));
        Assert.assertFalse(rules.loggedInUsersCanSeeAllQuestions(questionResource, anonymousUser));
    }
}
