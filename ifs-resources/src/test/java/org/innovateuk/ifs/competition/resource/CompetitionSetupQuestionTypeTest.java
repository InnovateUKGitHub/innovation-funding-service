package org.innovateuk.ifs.competition.resource;


import org.junit.Assert;
import org.junit.Test;

public class CompetitionSetupQuestionTypeTest {

    @Test
    public void testTypeFromQuestionTitle() throws Exception {

        Assert.assertEquals(
                CompetitionSetupQuestionType.APPLICATION_DETAILS,
                CompetitionSetupQuestionType.typeFromQuestionTitle("Application details"));
    }

    @Test
    public void testTypeFromQuestionWithUnrecognisedTitle() throws Exception {

        Assert.assertEquals(
                CompetitionSetupQuestionType.ASSESSED_QUESTION,
                CompetitionSetupQuestionType.typeFromQuestionTitle("Any title"));
    }
}
