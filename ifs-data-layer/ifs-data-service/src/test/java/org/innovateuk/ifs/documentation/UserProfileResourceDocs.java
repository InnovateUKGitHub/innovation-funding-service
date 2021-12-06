package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.user.builder.UserProfileResourceBuilder;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.user.builder.UserProfileResourceBuilder.newUserProfileResource;
import static org.innovateuk.ifs.user.resource.Title.Mr;

public class UserProfileResourceDocs {

    public static final UserProfileResourceBuilder userProfileResourceBuilder = newUserProfileResource()
            .withUser(1L)
            .withTitle(Mr)
            .withFirstName("First")
            .withLastName("Last")
            .withPhoneNumber("012434 567890")
            .withAddress(newAddressResource().withAddressLine1("Electric Works").withTown("Sheffield").withPostcode("S1 2BJ").build())
            .withEmail("tom@poly.io");
}