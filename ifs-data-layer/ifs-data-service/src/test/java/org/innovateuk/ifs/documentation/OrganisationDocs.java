package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.user.builder.OrganisationResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static com.google.common.primitives.Longs.asList;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.address.builder.AddressTypeResourceBuilder.newAddressTypeResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationAddressResourceBuilder.newOrganisationAddressResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class OrganisationDocs {

    public static final FieldDescriptor[] organisationResourceFields = {
            fieldWithPath("id").description("Id of the organisaiton"),
            fieldWithPath("name").description("Name of the organisation"),
            fieldWithPath("companyHouseNumber").description("The companies house number"),
            fieldWithPath("processRoles").description("Ids of the process roles"),
            fieldWithPath("applicationFinances").description("Ids of the applications finances"),
            fieldWithPath("addresses").description("List of organisation addresses"),
            fieldWithPath("users").description("Ids of the users for this organisation"),
            fieldWithPath("organisationType").description("Id of the organisation type"),
            fieldWithPath("organisationTypeName").description("Name of the organisation type"),
            fieldWithPath("organisationTypeDescription").description("Description of the organisation type")
    };

    public static final OrganisationResourceBuilder organisationResourceBuilder = newOrganisationResource()
            .withId(1L)
            .withName("Company name")
            .withCompanyHouseNumber("0123456789")
            .withProcessRoles(asList(1L, 2L))
            .withAddress(newOrganisationAddressResource()
                    .withAddress(newAddressResource()
                            .withAddressLine1("Address line 1")
                            .withAddressLine2("Address line 2")
                            .withAddressLine3("Address line 3")
                            .withCounty("County")
                            .withPostcode("Postcode")
                            .build())
                    .withAddressType(newAddressTypeResource()
                            .withName("Address type")
                            .build())
                    .build(1))
            .withUsers(asList(1L, 2L))
            .withOrganisationType(1L)
            .withOrganisationTypeName("Organisation type");
}
