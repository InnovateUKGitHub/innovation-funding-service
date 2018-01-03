package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.assessment.builder.AssessmentReviewRejectOutcomeResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.assessment.builder.AssessmentReviewRejectOutcomeResourceBuilder.newAssessmentReviewRejectOutcomeResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.CONFLICT_OF_INTEREST;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessmentReviewRejectOutcomeDocs {
    public static final FieldDescriptor[] assessmentReviewRejectOutcomeResourceFields = {
            fieldWithPath("rejectReason").description("The reason for rejecting the assessment review."),
            fieldWithPath("rejectComment").description("Any other comments about the reason why this application is being rejected.")
    };

    public static final AssessmentReviewRejectOutcomeResourceBuilder assessmentReviewRejectOutcomeResourceBuilder = newAssessmentReviewRejectOutcomeResource()
            .withRejectReason(CONFLICT_OF_INTEREST)
            .withRejectComment("Member of board of directors");
}
