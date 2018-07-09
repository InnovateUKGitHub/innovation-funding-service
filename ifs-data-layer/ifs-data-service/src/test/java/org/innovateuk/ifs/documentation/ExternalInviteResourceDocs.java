package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ExternalInviteResourceDocs {
    public static final FieldDescriptor[] externalInviteResourceFields = {
            fieldWithPath("name").description("name of the external invite"),
            fieldWithPath("organisationName").description("organisation name of the external invite"),
            fieldWithPath("organisationId").description("organisation id of the external invite"),
            fieldWithPath("email").description("email of the external invite"),
            fieldWithPath("applicationId").description("application id of the external invite"),
            fieldWithPath("status").description("name of the external invite"),
    };
}
