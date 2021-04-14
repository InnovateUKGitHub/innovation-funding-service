package org.innovateuk.ifs.management.competition.setup.completionstage.util;

import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;

public class CompletionStageUtilsTest {

    private CompletionStageUtils completionStageUtils;

    @Before
    public void setup() {
        completionStageUtils = new CompletionStageUtils();
        ReflectionTestUtils.setField(completionStageUtils, "alwaysOpenCompetitionEnabled", true);
    }

    @Test
    public void isAlwaysOpenCompetitionEnabled() {
        assertEquals(true, completionStageUtils.isAlwaysOpenCompetitionEnabled());
    }

    @Test
    public void isApplicationSubmissionEnabledWhenAlwaysOpenCompetitionEnabled() {
        assertEquals(true, completionStageUtils.isApplicationSubmissionEnabled(CompetitionCompletionStage.RELEASE_FEEDBACK));
        assertEquals(true, completionStageUtils.isApplicationSubmissionEnabled(CompetitionCompletionStage.PROJECT_SETUP));
        assertEquals(false, completionStageUtils.isApplicationSubmissionEnabled(CompetitionCompletionStage.COMPETITION_CLOSE));
    }

    @Test
    public void isApplicationSubmissionEnabledWhenAlwaysOpenCompetitionDisabled() {
        ReflectionTestUtils.setField(completionStageUtils, "alwaysOpenCompetitionEnabled", false);
        assertEquals(false, completionStageUtils.isApplicationSubmissionEnabled(CompetitionCompletionStage.RELEASE_FEEDBACK));
        assertEquals(false, completionStageUtils.isApplicationSubmissionEnabled(CompetitionCompletionStage.PROJECT_SETUP));
        assertEquals(false, completionStageUtils.isApplicationSubmissionEnabled(CompetitionCompletionStage.COMPETITION_CLOSE));
    }
}
