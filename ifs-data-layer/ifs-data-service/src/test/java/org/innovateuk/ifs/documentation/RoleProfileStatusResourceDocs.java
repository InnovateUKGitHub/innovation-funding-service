package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.user.builder.RoleProfileStatusResourceBuilder;
import org.innovateuk.ifs.user.resource.ProfileRole;
import org.innovateuk.ifs.user.resource.RoleProfileState;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.user.builder.RoleProfileStatusResourceBuilder.newRoleProfileStatusResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class RoleProfileStatusResourceDocs {

    public static final FieldDescriptor[] roleProfileStatusResourceFields = {
            fieldWithPath("userId").description("Id of the user"),
            fieldWithPath("roleProfileState").description("State of the users role, i.e. ACTIVE, UNAVAILABLE"),
            fieldWithPath("profileRole").description("The role which the state applies to"),
            fieldWithPath("description").description("reason to why user has current state"),
            fieldWithPath("createdBy").description("user who created this user"),
            fieldWithPath("createdOn").description("when the user was created"),
            fieldWithPath("modifiedBy").description("user who modified this user"),
            fieldWithPath("modifiedOn").description("when the user was modified")
    };

    public static final RoleProfileStatusResourceBuilder roleProfileStatusResourceBuilder = newRoleProfileStatusResource()
            .withUserId(1L)
            .withRoleProfileState(RoleProfileState.ACTIVE)
            .withProfileRole(ProfileRole.ASSESSOR)
            .withDescription("Description");
}
