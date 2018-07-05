package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class PostResourceDocs {

    public static final FieldDescriptor[] postResourceFields = {
            fieldWithPath("id").description("Id of the post"),
    };
}
