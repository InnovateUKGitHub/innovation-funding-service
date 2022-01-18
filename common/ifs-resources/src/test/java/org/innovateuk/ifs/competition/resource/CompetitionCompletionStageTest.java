package org.innovateuk.ifs.competition.resource;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CompetitionCompletionStageTest {

    @Test
    public void testAlwaysOpenValues() {

        List<CompetitionCompletionStage> alwaysOpenValues = CompetitionCompletionStage.alwaysOpenValues();

        assertEquals(2, alwaysOpenValues.size());
        assertEquals(Arrays.asList(CompetitionCompletionStage.RELEASE_FEEDBACK,
                CompetitionCompletionStage.PROJECT_SETUP), alwaysOpenValues);
    }
}
