package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
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
    public void setup() throws Exception {
        loggedInUser = newUserResource().build();
        questionResource = newQuestionResource().build();
    }

    @Test
    public void testAllUsersCanSeeQuestions() {
        assertTrue(rules.loggedInUsersCanSeeAllQuestions(questionResource, loggedInUser));
        assertFalse(rules.loggedInUsersCanSeeAllQuestions(questionResource, anonymousUser()));
    }

    @Test
    public void testNoUserCanUpdateAny() {
        assertFalse(rules.noUserCanUpdateAny(questionResource, loggedInUser));
    }
}
