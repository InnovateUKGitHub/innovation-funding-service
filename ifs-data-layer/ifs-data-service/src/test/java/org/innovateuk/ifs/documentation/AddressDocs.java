package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.address.builder.AddressResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AddressDocs {
    public static final FieldDescriptor[] addressResourceFields = {
            fieldWithPath("addressLine1").description("first addressLine"),
            fieldWithPath("addressLine2").description("second addressLine"),
            fieldWithPath("addressLine3").description("third addressLine"),
            fieldWithPath("town").description("fourth addressLine"),
            fieldWithPath("county").description("county where requested address is located"),
            fieldWithPath("postcode").description("postcode of the requested address"),
            fieldWithPath("country").description("country of the requested address")
    };

    public static final AddressResourceBuilder addressResourceBuilder = newAddressResource()
            .withAddressLine1("addressLine1")
            .withAddressLine2("addressLine2")
            .withAddressLine3("addressLine3")
            .withTown("regular town")
            .withCounty("regular county")
            .withPostcode("PD65OQ")
            .withCountry("");
}
