package org.innovateuk.ifs.competition.resource;

import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CompetitionSetupQuestionResourceTest {

    private CompetitionSetupQuestionResource resource;

    @Before
    public void setUp() {
        resource = new CompetitionSetupQuestionResource();
    }

    @Test
    public void guidanceRequiredForNullQuestionType() {
        // given
        resource.setType(null);

        // when
        boolean result = resource.isGuidanceRequired();

        // then
        assertTrue(result);
    }

    @Test
    public void guidanceNotRequiredForEdiQuestionType() {
        // given
        resource.setType(QuestionSetupType.EQUALITY_DIVERSITY_INCLUSION);

        // when
        boolean result = resource.isGuidanceRequired();

        // then
        assertFalse(result);
    }

    @Test
    public void guidanceRequiredForOtherQuestionType() {
        // given
        resource.setType(QuestionSetupType.APPLICATION_DETAILS);

        // when
        boolean result = resource.isGuidanceRequired();

        // then
        assertTrue(result);
    }
}
