package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class FileEntryDocs {
    public static final FieldDescriptor[] fileEntryResourceFields = {
            fieldWithPath("id").description("Id of the Monitoring Officer"),
            fieldWithPath("name").description("First name of the Monitoring Officer"),
            fieldWithPath("mediaType").description("Last name of the Monitoring Officer"),
            fieldWithPath("filesizeBytes").description("Email address of the Monitoring Officer")
    };

    public static final FieldDescriptor[] fileAndContentsFields = {
            fieldWithPath("fileEntry").description("Project file entry"),
            fieldWithPath("contentsSupplier").description("Project file supplier input stream")
    };
}
