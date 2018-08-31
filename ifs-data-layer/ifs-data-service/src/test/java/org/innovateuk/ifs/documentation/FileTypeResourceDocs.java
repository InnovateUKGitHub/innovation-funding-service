package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class FileTypeResourceDocs {

    public static final FieldDescriptor[] fileTypeResourceFields = {
            fieldWithPath("id").type("Number").description("The id of the file type"),
            fieldWithPath("name").type("String").description("The file type name"),
            fieldWithPath("extension").type("String").description("The extensions supported by this file type")
    };
}


