package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.address.builder.AddressResourceBuilder;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;

public class AddressDocs {
    public static final AddressResourceBuilder addressResourceBuilder = newAddressResource()
            .withAddressLine1("addressLine1")
            .withAddressLine2("addressLine2")
            .withAddressLine3("addressLine3")
            .withTown("regular town")
            .withCounty("regular county")
            .withPostcode("PD65OQ")
            .withCountry("");
}
