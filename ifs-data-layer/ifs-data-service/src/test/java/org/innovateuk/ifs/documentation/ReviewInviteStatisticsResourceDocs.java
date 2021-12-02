package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.review.builder.ReviewInviteStatisticsResourceBuilder;

import static org.innovateuk.ifs.review.builder.ReviewInviteStatisticsResourceBuilder.newReviewInviteStatisticsResource;

public class ReviewInviteStatisticsResourceDocs {

    public static final ReviewInviteStatisticsResourceBuilder reviewInviteStatisticsResourceBuilder =
            newReviewInviteStatisticsResource()
                    .withAssessorsInvited(11)
                    .withAssessorsAccepted(3)
                    .withAssessorsRejected(2);
}