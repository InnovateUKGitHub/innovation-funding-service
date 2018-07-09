package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ContentEventResourceDocs {
    public static final FieldDescriptor[] contentEventResourceFields = {
            fieldWithPath("id").description("Id of the content event"),
            fieldWithPath("publicContent").description("Public content of the content event"),
            fieldWithPath("date").description("Date of the content event"),
            fieldWithPath("content").description("Content of the content event"),
    };
}
