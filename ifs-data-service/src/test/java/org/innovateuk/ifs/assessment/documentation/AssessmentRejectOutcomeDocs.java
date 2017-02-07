package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.assessment.builder.AssessmentRejectOutcomeResourceBuilder;
import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.assessment.builder.AssessmentRejectOutcomeResourceBuilder.newAssessmentRejectOutcomeResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.CONFLICT_OF_INTEREST;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessmentRejectOutcomeDocs {
    public static final FieldDescriptor[] assessmentRejectOutcomeResourceFields = {
            fieldWithPath("rejectReason").description("The reason for rejecting the assessment."),
            fieldWithPath("rejectComment").description("Any other comments about the reason why this application is being rejected.")
    };

    public static final AssessmentRejectOutcomeResourceBuilder assessmentRejectOutcomeResourceBuilder = newAssessmentRejectOutcomeResource()
            .withRejectReason(CONFLICT_OF_INTEREST)
            .withRejectComment("Member of board of directors");
}
