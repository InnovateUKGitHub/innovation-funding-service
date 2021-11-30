package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.review.builder.ReviewResourceBuilder;

import java.time.LocalDate;

import static org.innovateuk.ifs.assessment.documentation.AssessmentReviewRejectOutcomeDocs.reviewRejectOutcomeResourceBuilder;
import static org.innovateuk.ifs.review.builder.ReviewResourceBuilder.newReviewResource;
import static org.innovateuk.ifs.review.resource.ReviewState.PENDING;

public class ReviewDocs {

    public static final ReviewResourceBuilder reviewResourceBuilder = newReviewResource()
            .withId(1L)
            .withStartDate(LocalDate.now())
            .withEndDate(LocalDate.now().plusDays(14))
            .withRejection(reviewRejectOutcomeResourceBuilder)
            .withActivityState(PENDING)
            .withProcessRole(1L)
            .withApplication(2L);
}
