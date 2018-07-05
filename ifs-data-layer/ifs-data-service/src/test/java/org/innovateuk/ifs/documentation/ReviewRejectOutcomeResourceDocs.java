package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ReviewRejectOutcomeResourceDocs {
    public static final FieldDescriptor[] reviewRejectOutcomeResourceFields = {
            fieldWithPath("reason").description("The reason for the rejection"),
    };
}
