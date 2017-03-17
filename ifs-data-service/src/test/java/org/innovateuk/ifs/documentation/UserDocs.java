package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

/**
 * Helper for Spring REST Docs, specifically for users.
 */
public class UserDocs {

    public static final FieldDescriptor[] userResourceFields = {
            fieldWithPath("id").description("id of the user"),
            fieldWithPath("uid").description("unique identifier returned from the authentication identity provider when the user is created"),
            fieldWithPath("firstName").description("first name of the user"),
            fieldWithPath("lastName").description("last name of the user"),
            fieldWithPath("inviteName").description("not used"),
            fieldWithPath("phoneNumber").description("telephone number of the user"),
            fieldWithPath("imageUrl").description("not used"),
            fieldWithPath("email").description("e-mail address of the user"),
            fieldWithPath("password").description("password of the user"),
            fieldWithPath("status").description("status of the user"),
            fieldWithPath("roles").description("roles that the user is associated with"),
            fieldWithPath("profileId").description("profile ID of the user")
    };

}
