package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.review.builder.ReviewInviteStatisticsResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.review.builder.ReviewInviteStatisticsResourceBuilder.newReviewInviteStatisticsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ReviewInviteStatisticsResourceDocs {
    public static final FieldDescriptor[] reviewInviteStatisticsResourceFields = {
            fieldWithPath("invited").description("The number of assessors invited to the assessment panel"),
            fieldWithPath("accepted").description("The number of assessors who have accepted an invitation to the assessment panel"),
            fieldWithPath("declined").description("The number of assessors who have rejected an invitation to the assessment panel"),
            fieldWithPath("pending").description("Deprecated. Will be removed in the next release")
    };

    public static final ReviewInviteStatisticsResourceBuilder reviewInviteStatisticsResourceBuilder =
            newReviewInviteStatisticsResource()
                    .withAssessorsInvited(11)
                    .withAssessorsAccepted(3)
                    .withAssessorsRejected(2);
}