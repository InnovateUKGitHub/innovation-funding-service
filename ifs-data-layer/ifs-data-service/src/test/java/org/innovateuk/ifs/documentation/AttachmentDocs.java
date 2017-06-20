package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AttachmentDocs {

    public static FieldDescriptor[] attachmentFields() {
        return new FieldDescriptor[] {
                fieldWithPath("id").description("Attachment unique id if already created"),
                fieldWithPath("name").description("The file name of the Attachment"),
                fieldWithPath("mediaType").description("The Media Type of the Attachment"),
                fieldWithPath("sizeInBytes").description("The size, in bytes, this attachment takes.")
        };
    }

}
