package org.innovateuk.ifs.form.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test the {@link QuestionPermissionRules}
 */
public class QuestionPermissionRulesTest extends BasePermissionRulesTest<QuestionPermissionRules> {

    private UserResource loggedInUser;
    private QuestionResource questionResource;

    @Override
    protected QuestionPermissionRules supplyPermissionRulesUnderTest() {
        return new QuestionPermissionRules();
    }

    @Before
    public void setup() {
        loggedInUser = newUserResource().build();
        questionResource = newQuestionResource().build();
    }

    @Test
    public void allUsersCanSeeQuestions() {
        assertTrue(rules.loggedInUsersCanSeeAllQuestions(questionResource, loggedInUser));
        assertFalse(rules.loggedInUsersCanSeeAllQuestions(questionResource, anonymousUser()));
    }

    @Test
    public void noUserCanUpdateAny() {
        assertFalse(rules.noUserCanUpdateAny(questionResource, loggedInUser));
    }
}
