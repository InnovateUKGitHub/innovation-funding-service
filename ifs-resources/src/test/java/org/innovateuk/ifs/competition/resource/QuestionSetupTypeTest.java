package org.innovateuk.ifs.competition.resource;


import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.junit.Assert;
import org.junit.Test;

public class QuestionSetupTypeTest {

    @Test
    public void testTypeFromQuestionTitle() throws Exception {

        Assert.assertEquals(
                QuestionSetupType.APPLICATION_DETAILS,
                QuestionSetupType.typeFromQuestionTitle("Application details"));
    }

    @Test
    public void testTypeFromQuestionWithUnrecognisedTitle() throws Exception {

        Assert.assertEquals(
                QuestionSetupType.ASSESSED_QUESTION,
                QuestionSetupType.typeFromQuestionTitle("Any title"));
    }
}
