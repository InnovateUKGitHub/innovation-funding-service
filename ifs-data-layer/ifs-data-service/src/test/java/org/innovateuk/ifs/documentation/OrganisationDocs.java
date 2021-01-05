package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class OrganisationDocs {

    public static final FieldDescriptor[] organisationResourceFields = {
            fieldWithPath("id").description("Id of the organisation").optional(),
            fieldWithPath("name").description("Name of the organisation").optional(),
            fieldWithPath("companiesHouseNumber").description("The companies house number").optional(),
            fieldWithPath("organisationType").description("Id of the organisation type").optional(),
            fieldWithPath("organisationTypeName").description("Name of the organisation type").optional(),
            fieldWithPath("organisationTypeDescription").description("Description of the organisation type").optional(),
            fieldWithPath("international").description("The organisation is located outside UK.").optional(),
            fieldWithPath("internationalRegistrationNumber").description("The international organisation registration number.").optional(),
            fieldWithPath("registrationNumber").description("The organisation registration number, used by knowledge bases.").optional(),
            fieldWithPath("addresses").description("The addresses of the organisation").optional(),
            fieldWithPath("dateOfIncorporation").description("The date the company was incorporation").optional(),
            fieldWithPath("sicCodes").description("The sic codes of the organisation").optional(),
            fieldWithPath("executiveOfficers").description("The executive offciers of the organisation.").optional(),
            fieldWithPath("organisationNumber").description("The number of the organisation, a unique identity number could be a charity number or UTC.").optional(),
            fieldWithPath("businessType").description("The type of the organisation , charity, partnership, etc.").optional()
    };

    public static final OrganisationResourceBuilder organisationResourceBuilder = newOrganisationResource()
            .withId(1L)
            .withName("Company name")
            .withCompaniesHouseNumber("0123456789")
            .withOrganisationType(1L)
            .withOrganisationTypeName("Organisation type");
}
