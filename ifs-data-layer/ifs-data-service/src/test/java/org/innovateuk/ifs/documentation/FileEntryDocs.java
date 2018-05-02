package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class FileEntryDocs {
    public static final FieldDescriptor[] fileEntryResourceFields = {
            fieldWithPath("id").type("Number").description("Id of the Monitoring Officer"),
            fieldWithPath("name").type("String").description("First name of the Monitoring Officer"),
            fieldWithPath("mediaType").type("String").description("Last name of the Monitoring Officer"),
            fieldWithPath("filesizeBytes").type("String").description("Email address of the Monitoring Officer")
    };

    public static final FieldDescriptor[] fileAndContentsFields = {
            fieldWithPath("fileEntry").description("Project file entry"),
            fieldWithPath("contentsSupplier").description("Project file supplier input stream")
    };
}
