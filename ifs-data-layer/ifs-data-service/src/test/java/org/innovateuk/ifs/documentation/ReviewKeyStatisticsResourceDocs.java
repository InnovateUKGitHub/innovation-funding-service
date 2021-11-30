package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.review.builder.ReviewKeyStatisticsResourceBuilder;

import static org.innovateuk.ifs.review.builder.ReviewKeyStatisticsResourceBuilder.newReviewKeyStatisticsResource;

public class ReviewKeyStatisticsResourceDocs {

    public static final ReviewKeyStatisticsResourceBuilder reviewKeyStatisticsResourceBuilder =
            newReviewKeyStatisticsResource()
                    .withApplicationsInPanel(5)
                    .withAssessorsAccepted(3)
                    .withAssessorsPending(2);
}
