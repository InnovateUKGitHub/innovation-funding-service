package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.review.resource.AssessmentReviewRejectOutcomeResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessmentReviewRejectOutcomeResourceBuilder.newAssessmentReviewRejectOutcomeResource;
import static org.junit.Assert.assertEquals;

public class AssessmentReviewRejectOutcomeResourceBuilderTest {

    @Test
    public void buildOne() {
        String expectedRejectComment = "Comment";

        AssessmentReviewRejectOutcomeResource assessmentRejectOutcomeResource = newAssessmentReviewRejectOutcomeResource()
                .withReason(expectedRejectComment)
                .build();

        assertEquals(expectedRejectComment, assessmentRejectOutcomeResource.getReason());
    }

    @Test
    public void buildMany() {
        String[] expectedRejectComments = {"Comment 1", "Comment 2"};

        List<AssessmentReviewRejectOutcomeResource> assessmentReviewRejectOutcomeResources = newAssessmentReviewRejectOutcomeResource()
                .withReason(expectedRejectComments)
                .build(2);

        AssessmentReviewRejectOutcomeResource first = assessmentReviewRejectOutcomeResources.get(0);

        assertEquals(expectedRejectComments[0], first.getReason());

        AssessmentReviewRejectOutcomeResource second = assessmentReviewRejectOutcomeResources.get(1);

        assertEquals(expectedRejectComments[1], second.getReason());
    }
}
