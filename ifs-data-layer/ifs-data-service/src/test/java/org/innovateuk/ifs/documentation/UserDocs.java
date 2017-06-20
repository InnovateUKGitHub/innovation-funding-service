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
            fieldWithPath("title").description("title of the user"),
            fieldWithPath("firstName").description("first name of the user"),
            fieldWithPath("lastName").description("last name of the user"),
            fieldWithPath("inviteName").description("not used"),
            fieldWithPath("phoneNumber").description("telephone number of the user"),
            fieldWithPath("imageUrl").description("not used"),
            fieldWithPath("email").description("e-mail address of the user"),
            fieldWithPath("password").description("password of the user"),
            fieldWithPath("status").description("status of the user"),
            fieldWithPath("roles").description("roles that the user is associated with"),
            fieldWithPath("gender").description("gender of the user"),
            fieldWithPath("disability").description("disability of the user"),
            fieldWithPath("ethnicity").description("ethnic group of the user"),
            fieldWithPath("profileId").description("profile ID of the user"),
            fieldWithPath("allowMarketingEmails").description("allow marketing emails")
    };

}
