package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.registration.builder.UserRegistrationResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.Collections;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static org.innovateuk.ifs.user.resource.Role.ASSESSOR;
import static org.innovateuk.ifs.user.resource.Title.Mr;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

/**
 * Helper for Spring REST Docs, specifically for user registration.
 */
public class UserRegistrationResourceDocs {

    public static final FieldDescriptor[] userRegistrationResourceFields = {
            fieldWithPath("title").description("title of the user"),
            fieldWithPath("firstName").description("first name of the user"),
            fieldWithPath("lastName").description("last name of the user"),
            fieldWithPath("phoneNumber").description("telephone number of the user"),
            fieldWithPath("password").description("password of the user"),
            fieldWithPath("address").description("assess of the user"),
            fieldWithPath("email").description("email address of the user"),
            fieldWithPath("roles").description("roles of the user")
    };

    public static final UserRegistrationResourceBuilder userRegistrationResourceBuilder = newUserRegistrationResource()
            .withTitle(Mr)
            .withFirstName("First")
            .withLastName("Last")
            .withPhoneNumber("012434 567890")
            .withPassword("Passw0rd123")
            .withAddress(newAddressResource().withAddressLine1("Electric Works").withTown("Sheffield").withPostcode("S1 2BJ").build())
            .withEmail("tom@poly.io")
            .withRoles(Collections.singletonList(ASSESSOR));
}