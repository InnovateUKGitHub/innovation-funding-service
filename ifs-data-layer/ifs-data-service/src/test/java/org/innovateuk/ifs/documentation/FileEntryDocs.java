package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class FileEntryDocs {
    public static final FieldDescriptor[] fileEntryResourceFields = {
            fieldWithPath("id").type("Number").description("Id of the File Entry"),
            fieldWithPath("name").type("String").description("Original filename of the File Entry"),
            fieldWithPath("mediaType").type("String").description("The media type of the File Entry e.g. application/pdf"),
            fieldWithPath("filesizeBytes").type("String").description("File size in bytes of the File Entry")
    };

    public static final FieldDescriptor[] fileAndContentsFields = {
            fieldWithPath("fileEntry").description("File entry"),
            fieldWithPath("contentsSupplier").description("File Entry supplier input stream")
    };
}
