package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.review.builder.ReviewKeyStatisticsResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.review.builder.ReviewKeyStatisticsResourceBuilder.newReviewKeyStatisticsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ReviewKeyStatisticsResourceDocs {
    public static final FieldDescriptor[] reviewKeyStatisticsResourceFields = {
            fieldWithPath("applicationsInPanel").description("The number of applications in the assessment panel"),
            fieldWithPath("assessorsPending").description("The number of assessors who haven't responded to an invitation to the assessment panel"),
            fieldWithPath("assessorsAccepted").description("The number of assessors who have accepted an invitation to the assessment panel")
    };

    public static final ReviewKeyStatisticsResourceBuilder reviewKeyStatisticsResourceBuilder =
            newReviewKeyStatisticsResource()
                    .withApplicationsInPanel(5)
                    .withAssessorsAccepted(3)
                    .withAssessorsPending(2);
}
