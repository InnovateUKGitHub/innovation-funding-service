package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class InviteUserResourceDocs {

    public static final FieldDescriptor[] inviteUserResourceFields = {
            fieldWithPath("invitedUser").description("The user for whom the invitation is being sent"),
            fieldWithPath("adminRoleType").description("The role of the invited user")
    };
}
