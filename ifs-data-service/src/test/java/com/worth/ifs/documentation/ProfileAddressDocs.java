package com.worth.ifs.documentation;

import com.worth.ifs.user.builder.ProfileAddressResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.user.builder.ProfileAddressResourceBuilder.newProfileAddressResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ProfileAddressDocs {
    public static final FieldDescriptor[] profileAddressResourceFields = {
            fieldWithPath("user").description("Assessor user associated with the profile address"),
            fieldWithPath("address").description("Address of the user"),
    };

    public static final ProfileAddressResourceBuilder profileAddressResourceBuilder = newProfileAddressResource()
            .withUser(1L)
            .withAddress(newAddressResource().build());
}
