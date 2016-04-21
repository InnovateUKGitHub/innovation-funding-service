package com.worth.ifs.documentation;

import com.worth.ifs.address.builder.AddressResourceBuilder;

import org.springframework.restdocs.payload.FieldDescriptor;

import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static java.util.Arrays.asList;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AddressDocs {
    public static final FieldDescriptor[] addressResourceFields = {
        fieldWithPath("id").description("id of the address"),
        fieldWithPath("addressLine1").description("first addressLine"),
        fieldWithPath("addressLine2").description("second addressLine"),
        fieldWithPath("addressLine3").description("third addressLine"),
        fieldWithPath("town").description("fourth addressLine"),
        fieldWithPath("county").description("county where requested address is located"),
        fieldWithPath("postcode").description("postcode of the requested address")
    };

    public static final AddressResourceBuilder addressResourceBuilder = newAddressResource()
            .withId(1L ,2L)
            .withAddressLine1("addressLine1")
            .withAddressLine2("addressLine2")
            .withAddressLine3("addressLine3")
            .withTown("regular town")
            .withCounty("regular county")
            .withPostcode("PD65OQ")
            .withOrganisationList(asList(1L, 2L, 3L));

}
