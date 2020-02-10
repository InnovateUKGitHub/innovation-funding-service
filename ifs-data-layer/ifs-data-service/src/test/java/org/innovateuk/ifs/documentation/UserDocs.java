package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.documentation.PageResourceDocs.pageResourceFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

/**
 * Helper for Spring REST Docs, specifically for users.
 */
public class UserDocs {

    public static final FieldDescriptor[] userResourceFields = {
            fieldWithPath("id").description("id of the user"),
            fieldWithPath("uid").description("unique identifier returned from the authentication identity provider when the user is created"),
            fieldWithPath("title").description("title of the user"),
            fieldWithPath("firstName").type("String").description("first name of the user"),
            fieldWithPath("lastName").type("String").description("last name of the user"),
            fieldWithPath("inviteName").description("not used"),
            fieldWithPath("phoneNumber").description("telephone number of the user"),
            fieldWithPath("imageUrl").description("not used"),
            fieldWithPath("email").type("String").description("e-mail address of the user"),
            fieldWithPath("password").description("password of the user"),
            fieldWithPath("status").description("status of the user"),
            fieldWithPath("roles").description("roles that the user is associated with"),
            fieldWithPath("profileId").description("profile ID of the user"),
            fieldWithPath("allowMarketingEmails").description("allow marketing emails"),
            fieldWithPath("termsAndConditionsIds").description("ids of accepted terms and conditions"),
            fieldWithPath("createdBy").description("user who created this user"),
            fieldWithPath("createdOn").description("when the user was created"),
            fieldWithPath("modifiedBy").description("user who modified this user"),
            fieldWithPath("modifiedOn").description("when the user was modified"),
    };

    public static final FieldDescriptor[] manageUserResourceFields = {
            fieldWithPath("id").description("id of the user"),
            fieldWithPath("name").type("String").description("full name of the user"),
            fieldWithPath("email").type("String").description("e-mail address of the user"),
            fieldWithPath("roles").description("roles that the user is associated with"),
            fieldWithPath("createdBy").description("user who created this user"),
            fieldWithPath("createdOn").description("when the user was created"),
            fieldWithPath("roleProfileStatusResourceSet").description("status of the roles the user holds"),
    };

    public static final FieldDescriptor[] internalUserRegistrationResourceFields = {
            fieldWithPath("firstName").description("first name of the user"),
            fieldWithPath("lastName").description("last name of the user"),
            fieldWithPath("email").description("e-mail address of the user"),
            fieldWithPath("password").description("password of the user"),
            fieldWithPath("roles").description("roles that the user is associated with"),
    };

    public static final FieldDescriptor[] simpleUserResourceFields = {
            fieldWithPath("id").description("id of the user"),
            fieldWithPath("firstName").type("String").description("first name of the user"),
            fieldWithPath("lastName").type("String").description("last name of the user"),
            fieldWithPath("email").type("String").description("e-mail address of the user"),
    };

    public static final FieldDescriptor[] userPageResourceFields = pageResourceFields;
}
