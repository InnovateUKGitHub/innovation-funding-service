package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.user.builder.UserProfileResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.user.builder.UserProfileResourceBuilder.newUserProfileResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class UserProfileResourceDocs {

    public static final FieldDescriptor[] userProfileResourceFields = {
            fieldWithPath("user").description("id of the user"),
            fieldWithPath("firstName").description("first name of the user"),
            fieldWithPath("lastName").description("last name of the user"),
            fieldWithPath("phoneNumber").description("telephone number of the user"),
            fieldWithPath("address").description("address of the user"),
            fieldWithPath("email").description("email address of the user"),
    };

    public static final UserProfileResourceBuilder userProfileResourceBuilder = newUserProfileResource()
            .withUser(1L)
            .withFirstName("First")
            .withLastName("Last")
            .withPhoneNumber("012434 567890")
            .withAddress(newAddressResource().withAddressLine1("Electric Works").withTown("Sheffield").withPostcode("S1 2BJ").build())
            .withEmail("tom@poly.io");
}
