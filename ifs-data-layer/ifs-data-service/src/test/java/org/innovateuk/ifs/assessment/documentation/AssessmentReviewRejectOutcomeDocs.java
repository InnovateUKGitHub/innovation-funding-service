package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.review.builder.ReviewRejectOutcomeResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.review.builder.ReviewRejectOutcomeResourceBuilder.newReviewRejectOutcomeResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessmentReviewRejectOutcomeDocs {
    public static final FieldDescriptor[] assessmentReviewRejectOutcomeResourceFields = {
            fieldWithPath("rejectComment").description("Any other comments about the reason why this application is being rejected.")
    };

    public static final ReviewRejectOutcomeResourceBuilder reviewRejectOutcomeResourceBuilder = newReviewRejectOutcomeResource()
            .withReason("Member of board of directors");
}
