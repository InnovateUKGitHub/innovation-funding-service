package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class PostResourceDocs {

    public static final FieldDescriptor[] postResourceFields = {
            fieldWithPath("id").description("Id of the post"),
            fieldWithPath("author").description("author of the post"),
            fieldWithPath("body").description("body of the post"),
            fieldWithPath("attachments").description("attachments of the post"),
            fieldWithPath("createdOn").description("created on of the post"),
    };
}
