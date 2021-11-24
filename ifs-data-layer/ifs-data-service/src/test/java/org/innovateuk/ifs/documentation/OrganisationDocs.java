package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder;

import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;

public class OrganisationDocs {

    public static final OrganisationResourceBuilder organisationResourceBuilder = newOrganisationResource()
            .withId(1L)
            .withName("Company name")
            .withCompaniesHouseNumber("0123456789")
            .withOrganisationType(1L)
            .withOrganisationTypeName("Organisation type");
}
