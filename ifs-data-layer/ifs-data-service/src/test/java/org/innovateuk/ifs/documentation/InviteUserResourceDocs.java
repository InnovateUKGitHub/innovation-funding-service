package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class InviteUserResourceDocs {

    public static final FieldDescriptor[] inviteUserResourceFields = {
            fieldWithPath("invitedUser").description("The user for whom the invitation is being sent"),
            fieldWithPath("role").type("String").description("The role of the invited user"),
            fieldWithPath("organisation").type("String").description("The organisation")
    };

    public static final FieldDescriptor[] roleInviteResourceFields = {
            fieldWithPath("id").description("The user to whom the invitation belongs"),
            fieldWithPath("name").description("Name of invited user"),
            fieldWithPath("email").description("Email of invited user"),
            fieldWithPath("role").description("The role for the invited user"),
            fieldWithPath("hash").description("Invite hash key"),
            fieldWithPath("organisation").description("The organisation")
    };
}
