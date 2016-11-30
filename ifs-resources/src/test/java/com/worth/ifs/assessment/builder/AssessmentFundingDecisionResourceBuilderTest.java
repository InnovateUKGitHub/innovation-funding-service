package com.worth.ifs.assessment.builder;

import com.worth.ifs.assessment.resource.AssessmentFundingDecisionResource;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.assessment.builder.AssessmentFundingDecisionResourceBuilder.newAssessmentFundingDecisionResource;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;

public class AssessmentFundingDecisionResourceBuilderTest {

    @Test
    public void buildOne() {
        Boolean expectedFundingConfirmation = FALSE;
        String expectedComment = "Comment";
        String expectedFeedback = "Feedback";

        AssessmentFundingDecisionResource assessmentFundingDecisionResource = newAssessmentFundingDecisionResource()
                .withFundingConfirmation(expectedFundingConfirmation)
                .withComment(expectedComment)
                .withFeedback(expectedFeedback)
                .build();

        assertEquals(expectedFundingConfirmation, assessmentFundingDecisionResource.getFundingConfirmation());
        assertEquals(expectedComment, assessmentFundingDecisionResource.getComment());
        assertEquals(expectedFeedback, assessmentFundingDecisionResource.getFeedback());
    }

    @Test
    public void buildMany() {
        Boolean[] expectedFundingConfirmations = {TRUE, FALSE};
        String[] expectedComments = {"Comment 1", "Comment 2"};
        String[] expectedFeedbacks = {"Feedback 1", "Feedback 2"};

        List<AssessmentFundingDecisionResource> assessmentFundingDecisionResources = newAssessmentFundingDecisionResource()
                .withFundingConfirmation(expectedFundingConfirmations)
                .withComment(expectedComments)
                .withFeedback(expectedFeedbacks)
                .build(2);

        AssessmentFundingDecisionResource first = assessmentFundingDecisionResources.get(0);

        assertEquals(expectedFundingConfirmations[0], first.getFundingConfirmation());
        assertEquals(expectedComments[0], first.getComment());
        assertEquals(expectedFeedbacks[0], first.getFeedback());

        AssessmentFundingDecisionResource second = assessmentFundingDecisionResources.get(1);

        assertEquals(expectedFundingConfirmations[1], second.getFundingConfirmation());
        assertEquals(expectedComments[1], second.getComment());
        assertEquals(expectedFeedbacks[1], second.getFeedback());
    }

}