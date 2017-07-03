package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class InviteUserResourceDocs {

    public static final FieldDescriptor[] inviteUserResourceFields = {
            fieldWithPath("invitedUser").description("The user for whom the invitation is being sent"),
            fieldWithPath("adminRoleType").description("The role of the invited user")
    };

    public static final FieldDescriptor[] roleInviteResourceFields = {
            fieldWithPath("id").description("The user to whom the invitation belongs"),
            fieldWithPath("name").description("Name of invited user"),
            fieldWithPath("email").description("Email of invited user"),
            fieldWithPath("roleId").description("The role to which user has been invited"),
            fieldWithPath("roleName").description("The name of role for the invited user"),
            fieldWithPath("hash").description("Invite hash key")
    };
}
