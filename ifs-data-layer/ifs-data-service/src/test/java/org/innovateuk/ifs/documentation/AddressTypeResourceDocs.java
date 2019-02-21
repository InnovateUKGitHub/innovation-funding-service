package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AddressTypeResourceDocs {

    public static final FieldDescriptor[] addressTypeResourceFields = {
            fieldWithPath("id").description("id of the address type"),
            fieldWithPath("name").description("name of the address type"),
    };
}
