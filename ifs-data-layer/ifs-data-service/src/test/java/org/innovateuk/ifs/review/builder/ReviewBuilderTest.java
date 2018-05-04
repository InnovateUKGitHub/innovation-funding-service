package org.innovateuk.ifs.review.builder;

import org.innovateuk.ifs.review.domain.Review;
import org.innovateuk.ifs.review.domain.ReviewRejectOutcome;
import org.innovateuk.ifs.review.resource.ReviewState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.review.builder.ReviewBuilder.newReview;
import static org.innovateuk.ifs.review.builder.ReviewRejectOutcomeBuilder.newReviewRejectOutcome;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.assertEquals;

public class ReviewBuilderTest {

    @Test
    public void buildOne() throws Exception {
        Long expectedId = 1L;
        ProcessState expectedStatus = ReviewState.PENDING;
        ProcessRole expectedProcessRole = newProcessRole().build();
        ReviewRejectOutcome expectedRejection = newReviewRejectOutcome().build();

        Review review = newReview()
                .withId(expectedId)
                .withState(ReviewState.PENDING)
                .withParticipant(expectedProcessRole)
                .withRejection(expectedRejection)
                .build();

        assertEquals(expectedId, review.getId());
        assertEquals(expectedStatus, review.getProcessState());
        assertEquals(expectedProcessRole, review.getParticipant());
        assertEquals(expectedRejection, review.getRejection());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        ProcessState[] expectedStatuses = {ReviewState.PENDING, ReviewState.ACCEPTED};
        ProcessRole[] expectedProcessRoles = newProcessRole().buildArray(2, ProcessRole.class);
        ReviewRejectOutcome[] expectedRejection = newReviewRejectOutcome()
                .buildArray(2, ReviewRejectOutcome.class);

        List<Review> reviews = newReview()
                .withId(expectedIds)
                .withState(ReviewState.PENDING, ReviewState.ACCEPTED)
                .withParticipant(expectedProcessRoles)
                .withRejection(expectedRejection)
                .build(2);

        Review first = reviews.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedStatuses[0], first.getProcessState());
        assertEquals(expectedProcessRoles[0], first.getParticipant());
        assertEquals(expectedRejection[0], first.getRejection());

        Review second = reviews.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedStatuses[1], second.getProcessState());
        assertEquals(expectedProcessRoles[1], second.getParticipant());
        assertEquals(expectedRejection[1], second.getRejection());
    }
}
