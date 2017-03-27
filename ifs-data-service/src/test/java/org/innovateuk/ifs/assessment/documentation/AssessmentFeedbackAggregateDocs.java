package org.innovateuk.ifs.assessment.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessmentFeedbackAggregateDocs {
    public static final FieldDescriptor[] assessmentFeedbackAggregateResourceFields = {
            fieldWithPath("avgScore").description("The average score from the assessors"),
            fieldWithPath("feedback").description("The list of feedback from assessors")
    };
}
