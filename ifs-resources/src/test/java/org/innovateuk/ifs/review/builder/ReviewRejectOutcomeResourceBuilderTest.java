package org.innovateuk.ifs.review.builder;

import org.innovateuk.ifs.review.resource.ReviewRejectOutcomeResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.review.builder.ReviewRejectOutcomeResourceBuilder.newReviewRejectOutcomeResource;
import static org.junit.Assert.assertEquals;

public class ReviewRejectOutcomeResourceBuilderTest {

    @Test
    public void buildOne() {
        String expectedRejectComment = "Comment";

        ReviewRejectOutcomeResource assessmentRejectOutcomeResource = newReviewRejectOutcomeResource()
                .withReason(expectedRejectComment)
                .build();

        assertEquals(expectedRejectComment, assessmentRejectOutcomeResource.getReason());
    }

    @Test
    public void buildMany() {
        String[] expectedRejectComments = {"Comment 1", "Comment 2"};

        List<ReviewRejectOutcomeResource> reviewRejectOutcomeResources = newReviewRejectOutcomeResource()
                .withReason(expectedRejectComments)
                .build(2);

        ReviewRejectOutcomeResource first = reviewRejectOutcomeResources.get(0);

        assertEquals(expectedRejectComments[0], first.getReason());

        ReviewRejectOutcomeResource second = reviewRejectOutcomeResources.get(1);

        assertEquals(expectedRejectComments[1], second.getReason());
    }
}
