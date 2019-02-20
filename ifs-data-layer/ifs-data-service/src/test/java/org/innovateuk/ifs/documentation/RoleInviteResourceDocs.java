package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class RoleInviteResourceDocs {
    public static final FieldDescriptor[] roleInviteResourceFields = {
            fieldWithPath("id").description("Id of the role invite"),
            fieldWithPath("name").description("Name of the role invite"),
            fieldWithPath("email").description("Email of the role invite"),
            fieldWithPath("roleId").description("Role id of the role invite"),
            fieldWithPath("roleName").description("Role name of the role invite"),
            fieldWithPath("hash").description("Hash of the role invite"),
    };
}
