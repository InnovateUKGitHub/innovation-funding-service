package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ProcessRoleDocs {
    public static final FieldDescriptor[] processRoleResourceFields = {
            fieldWithPath("id").description("Id of the process role"),
            fieldWithPath("user").description("User of the process role"),
            fieldWithPath("userName").description("User name of the process role"),
            fieldWithPath("applicationId").description("Application Id of the process role"),
            fieldWithPath("role").description("Role of the process role"),
            fieldWithPath("roleName").description("Role name of the process role"),
            fieldWithPath("organisationId").description("Organisation id of the process role"),
    };
}
