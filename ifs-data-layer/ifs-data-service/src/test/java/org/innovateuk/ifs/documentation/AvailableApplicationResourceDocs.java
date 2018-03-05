package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AvailableApplicationResourceDocs {

    public static final FieldDescriptor[] availableApplicationResourceFields = {
        fieldWithPath("id").description("Id of the resource"),
        fieldWithPath("name").description("Name of the Application"),
        fieldWithPath("leadOrganisation").description("The lead organisation of this project"),
    };
}
