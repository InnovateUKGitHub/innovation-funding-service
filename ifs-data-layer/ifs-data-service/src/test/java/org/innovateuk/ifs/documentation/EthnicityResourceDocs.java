package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class EthnicityResourceDocs {


    public static final FieldDescriptor[] ethnicityResourceFields = {
            fieldWithPath("id").description("id of the ethnicity"),
            fieldWithPath("name").description("name of the ethnicity"),
            fieldWithPath("description").description("description of the ethnicity"),
            fieldWithPath("priority").description("priority of the ethnicity"),
    };
}
