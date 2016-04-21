package com.worth.ifs.application.security;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.user.resource.UserResource;

import org.junit.Test;

import static com.worth.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertTrue;

public class QuestionRulesTest {

    @Test
    public void allUsersCanSeeQuestions(){
        QuestionResource qr = newQuestionResource().build();
        UserResource ur = newUserResource().build();
        assertTrue(QuestionRules.allUsersCanSeeAllQuestions(qr, ur));
    }
}
