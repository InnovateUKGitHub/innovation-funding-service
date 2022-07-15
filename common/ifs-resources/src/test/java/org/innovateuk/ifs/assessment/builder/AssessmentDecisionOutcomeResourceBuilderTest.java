package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.resource.AssessmentDecisionOutcomeResource;
import org.junit.Test;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.assessment.builder.AssessmentDecisionOutcomeResourceBuilder.newAssessmentDecisionOutcomeResource;
import static org.junit.Assert.assertEquals;

public class AssessmentDecisionOutcomeResourceBuilderTest {

    @Test
    public void buildOne() {
        Boolean expectedFundingConfirmation = FALSE;
        String expectedComment = "Comment";
        String expectedFeedback = "Feedback";

        AssessmentDecisionOutcomeResource assessmentDecisionOutcomeResource = newAssessmentDecisionOutcomeResource()
                .withFundingConfirmation(expectedFundingConfirmation)
                .withComment(expectedComment)
                .withFeedback(expectedFeedback)
                .build();

        assertEquals(expectedFundingConfirmation, assessmentDecisionOutcomeResource.getFundingConfirmation());
        assertEquals(expectedComment, assessmentDecisionOutcomeResource.getComment());
        assertEquals(expectedFeedback, assessmentDecisionOutcomeResource.getFeedback());
    }

    @Test
    public void buildMany() {
        Boolean[] expectedFundingConfirmations = {TRUE, FALSE};
        String[] expectedComments = {"Comment 1", "Comment 2"};
        String[] expectedFeedbacks = {"Feedback 1", "Feedback 2"};

        List<AssessmentDecisionOutcomeResource> assessmentDecisionOutcomeResources = newAssessmentDecisionOutcomeResource()
                .withFundingConfirmation(expectedFundingConfirmations)
                .withComment(expectedComments)
                .withFeedback(expectedFeedbacks)
                .build(2);

        AssessmentDecisionOutcomeResource first = assessmentDecisionOutcomeResources.get(0);

        assertEquals(expectedFundingConfirmations[0], first.getFundingConfirmation());
        assertEquals(expectedComments[0], first.getComment());
        assertEquals(expectedFeedbacks[0], first.getFeedback());

        AssessmentDecisionOutcomeResource second = assessmentDecisionOutcomeResources.get(1);

        assertEquals(expectedFundingConfirmations[1], second.getFundingConfirmation());
        assertEquals(expectedComments[1], second.getComment());
        assertEquals(expectedFeedbacks[1], second.getFeedback());
    }

}
