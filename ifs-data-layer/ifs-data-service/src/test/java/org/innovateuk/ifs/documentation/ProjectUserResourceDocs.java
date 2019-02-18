package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ProjectUserResourceDocs {
    public static final FieldDescriptor[] projectUserResourceFields = {
            fieldWithPath("id").description("Id of the project user"),
            fieldWithPath("user").description("User of the project user"),
            fieldWithPath("userName").description("User name of the project user"),
            fieldWithPath("project").description("Project of the project user"),
            fieldWithPath("role").description("Role of the project user"),
            fieldWithPath("roleName").description("Role name of the project user"),
            fieldWithPath("organisation").description("Organisation of the project user"),
            fieldWithPath("email").description("Email of the project user"),
            fieldWithPath("phoneNumber").description("Phone number of the project user"),
            fieldWithPath("invite").description("Invite of the project user"),
    };
}
