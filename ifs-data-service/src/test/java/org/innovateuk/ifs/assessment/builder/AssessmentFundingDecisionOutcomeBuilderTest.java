package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.domain.AssessmentFundingDecisionOutcome;
import org.junit.Test;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeBuilder.newAssessmentFundingDecisionOutcome;
import static org.junit.Assert.assertEquals;

public class AssessmentFundingDecisionOutcomeBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        boolean expectedFundingConfirmation = false;
        String expectedComment = "Comment";
        String expectedFeedback = "Feedback";

        AssessmentFundingDecisionOutcome assessmentFundingDecisionOutcome = newAssessmentFundingDecisionOutcome()
                .withId(expectedId)
                .withFundingConfirmation(expectedFundingConfirmation)
                .withComment(expectedComment)
                .withFeedback(expectedFeedback)
                .build();

        assertEquals(expectedFundingConfirmation, assessmentFundingDecisionOutcome.isFundingConfirmation());
        assertEquals(expectedComment, assessmentFundingDecisionOutcome.getComment());
        assertEquals(expectedFeedback, assessmentFundingDecisionOutcome.getFeedback());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        Boolean[] expectedFundingConfirmations = {TRUE, FALSE};
        String[] expectedComments = {"Comment 1", "Comment 2"};
        String[] expectedFeedbacks = {"Feedback 1", "Feedback 2"};

        List<AssessmentFundingDecisionOutcome> assessmentFundingDecisionOutcomes = newAssessmentFundingDecisionOutcome()
                .withId(expectedIds)
                .withFundingConfirmation(expectedFundingConfirmations)
                .withComment(expectedComments)
                .withFeedback(expectedFeedbacks)
                .build(2);

        AssessmentFundingDecisionOutcome first = assessmentFundingDecisionOutcomes.get(0);

        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedFundingConfirmations[0], first.isFundingConfirmation());
        assertEquals(expectedComments[0], first.getComment());
        assertEquals(expectedFeedbacks[0], first.getFeedback());

        AssessmentFundingDecisionOutcome second = assessmentFundingDecisionOutcomes.get(1);

        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedFundingConfirmations[1], second.isFundingConfirmation());
        assertEquals(expectedComments[1], second.getComment());
        assertEquals(expectedFeedbacks[1], second.getFeedback());
    }

}