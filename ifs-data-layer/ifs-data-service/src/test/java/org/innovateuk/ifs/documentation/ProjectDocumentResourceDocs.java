package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ProjectDocumentResourceDocs {

    public static final FieldDescriptor[] projectDocumentResourceFields = {
            fieldWithPath("id").description("The id of the Project Document").optional(),
            fieldWithPath("competition").type("Number").description("The competition to which this Project Document belongs to").optional(),
            fieldWithPath("title").type("String").description("The title of the Project Document").optional(),
            fieldWithPath("guidance").type("String").description("The guidance for the Project Document").optional(),
            fieldWithPath("editable").description("Whether this Project Document is editable or not").optional(),
            fieldWithPath("enabled").description("Whether this Project Document is enabled or not").optional(),
            fieldWithPath("fileTypes").type("Array").description("The supported fileType ids by this Project Document")
    };
}


