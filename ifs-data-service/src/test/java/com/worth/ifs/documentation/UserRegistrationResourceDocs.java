package com.worth.ifs.documentation;

import com.worth.ifs.registration.builder.UserRegistrationResourceBuilder;
import com.worth.ifs.user.resource.Disability;
import com.worth.ifs.user.resource.Gender;
import org.springframework.restdocs.payload.FieldDescriptor;

import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static com.worth.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.resource.UserRoleType.ASSESSOR;
import static java.util.Arrays.asList;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

/**
 * Helper for Spring REST Docs, specifically for user registration.
 */
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.id;
public class UserRegistrationResourceDocs {

    public static final FieldDescriptor[] userRegistrationResourceFields = {
            fieldWithPath("title").description("title of the user"),
            fieldWithPath("firstName").description("first name of the user"),
            fieldWithPath("lastName").description("last name of the user"),
            fieldWithPath("phoneNumber").description("telephone number of the user"),
            fieldWithPath("gender").description("gender of the user"),
            fieldWithPath("disability").description("disability of the user"),
            fieldWithPath("ethnicity").description("ethnic group of the user"),
            fieldWithPath("password").description("password of the user"),
            fieldWithPath("address").description("assess of the user"),
            fieldWithPath("email").description("email address of the user"),
            fieldWithPath("roles").description("roles of the user")
    };

    public static final UserRegistrationResourceBuilder userRegistrationResourceBuilder = newUserRegistrationResource()
            .withTitle("Mr")
            .withFirstName("First")
            .withLastName("Last")
            .withPhoneNumber("012434 567890")
            .withGender(Gender.MALE)
            .withDisability(Disability.NOT_STATED)
            .withEthnicity(newEthnicityResource().with(id(1L)).build())
            .withPassword("Passw0rd123")
            .withAddress(newAddressResource().withAddressLine1("Electric Works").withTown("Sheffield").withPostcode("S1 2BJ").build())
            .withEmail("tom@poly.io")
            .withRoles(asList(newRoleResource().withId(3L).withType(ASSESSOR).build()));
}