package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class OrganisationDocs {

    public static final OrganisationResourceBuilder organisationResourceBuilder = newOrganisationResource()
            .withId(1L)
            .withName("Company name")
            .withCompaniesHouseNumber("0123456789")
            .withOrganisationType(1L)
            .withOrganisationTypeName("Organisation type");
}
