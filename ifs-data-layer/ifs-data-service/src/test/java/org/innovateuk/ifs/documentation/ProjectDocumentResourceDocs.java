package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ProjectDocumentResourceDocs {

    public static final FieldDescriptor[] projectDocumentResourceFields = {
            fieldWithPath("id").description("The id of the Project Document"),
            fieldWithPath("competition").type("Number").description("The competition to which this Project Document belongs to"),
            fieldWithPath("title").type("String").description("The title of the Project Document"),
            fieldWithPath("guidance").type("String").description("The guidance for the Project Document"),
            fieldWithPath("editable").description("Whether this Project Document is editable or not"),
            fieldWithPath("enabled").description("Whether this Project Document is enabled or not"),
            fieldWithPath("fileTypes").type("Array").description("The supported fileType ids by this Project Document")
    };
}


