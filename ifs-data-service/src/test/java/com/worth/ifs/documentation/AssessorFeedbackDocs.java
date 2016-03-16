package com.worth.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessorFeedbackDocs {
    public static final FieldDescriptor[] assessorFeedbackResourceFields = {
            fieldWithPath("id").description("Id of the assessorFeedback"),
            fieldWithPath("response").description("Id of the response linked to the feedback"),
            fieldWithPath("assessor").description("Id of the assessor responsible for the feedback"),
            fieldWithPath("assessorId").ignored(),
            fieldWithPath("assessmentValue").description("numeric value given to the response"),
            fieldWithPath("assessmentFeedback").description("textual explanation of th assessmentValue"),
            fieldWithPath("wordCount").description("amount of words used in the assessmentFeedback"),
            fieldWithPath("wordCountLeft").description("number of words that can still be used to write the assessmentFeedback")
    };
}
