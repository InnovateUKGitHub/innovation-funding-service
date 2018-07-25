package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class UserOrganisationResourceDocs {

    public static final FieldDescriptor[] userOrganisationResourceFields = {
            fieldWithPath("name").description("name of the user"),
            fieldWithPath("organisationName").description("name of the organisation"),
            fieldWithPath("organisationId").description("id of the organisation"),
            fieldWithPath("email").description("email of the user"),
            fieldWithPath("status").description("status of the user organisation"),
            fieldWithPath("organisationType").description("type of the user organisation")
    };
}
