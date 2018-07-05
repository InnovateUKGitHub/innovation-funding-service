package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class RejectionReasonResourceDocs {

    public static final FieldDescriptor[] rejectionReasonResourceFields = {
            fieldWithPath("id").description("The id of the rejection reason"),
            fieldWithPath("reason").description("The reason of the rejection reason"),
            fieldWithPath("active").description("The active of the rejection reason"),
            fieldWithPath("priority").description("The priority of the rejection reason"),
    };
}
