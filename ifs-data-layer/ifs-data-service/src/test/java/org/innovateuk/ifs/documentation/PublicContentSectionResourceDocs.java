package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class PublicContentSectionResourceDocs {

    public static final FieldDescriptor[] publicContentSectionResourceFields = {
            fieldWithPath("id").description("Id of the public content section"),
            fieldWithPath("publicContent").description("Public content of the public content section"),
            fieldWithPath("type").description("Type of the public content section"),
            fieldWithPath("status").description("Status of the public content section"),
            fieldWithPath("contentGroups").description("Public content groups of the public content section"),

    };
}
