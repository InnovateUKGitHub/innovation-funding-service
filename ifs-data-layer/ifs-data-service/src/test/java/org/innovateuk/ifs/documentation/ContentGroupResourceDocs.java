package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ContentGroupResourceDocs {
    public static final FieldDescriptor[] contentGroupResourceFields = {
            fieldWithPath("id").description("Id of the content group"),
            fieldWithPath("contentSectionId").description("Content section id of the content group"),
            fieldWithPath("sectionType").description("Section type of the content group"),
            fieldWithPath("priority").description("Priority of the content group"),
            fieldWithPath("fileEntry").description("File entry of the content group"),
            fieldWithPath("heading").description("Heading of the content group"),
            fieldWithPath("content").description("Content of the content group"),
    };
}
