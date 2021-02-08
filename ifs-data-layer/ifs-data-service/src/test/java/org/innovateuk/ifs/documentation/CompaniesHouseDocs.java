package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompaniesHouseDocs {

    public static FieldDescriptor[] organisationSearchResultFields () {
        return new FieldDescriptor[] {
                fieldWithPath("organisationSearchId").description("Id of the Organisation Search Result"),
                fieldWithPath("name").description("Name of the associated Organisation"),
                fieldWithPath("organisationAddress").description("The associated Organisation's Address"),
                fieldWithPath("organisationAddress.addressLine1").description("The first line of the Organisation's Address"),
                fieldWithPath("organisationAddress.addressLine2").description("The second line of the Organisation's Address"),
                fieldWithPath("organisationAddress.addressLine3").description("The third line of the Organisation's Address"),
                fieldWithPath("organisationAddress.town").description("The town of the Organisation's Address"),
                fieldWithPath("organisationAddress.county").description("The county of Organisation's Address"),
                fieldWithPath("organisationAddress.postcode").description("The postcode of the Organisation's Address"),
                fieldWithPath("organisationAddress.country").description("The country of the Organisation's Address"),
                fieldWithPath("extraAttributes").description("Extra attributes of the Organisation"),
                fieldWithPath("organisationSicCodes[].id").description("Generated Id of the organisation"),
                fieldWithPath("organisationSicCodes[].organisation").description("Organisation"),
                fieldWithPath("organisationSicCodes[].sicCode").description("Sic Codes of the organisation"),
                fieldWithPath("organisationExecutiveOfficers[].id").description("Generated Id of the organisation"),
                fieldWithPath("organisationExecutiveOfficers[].organisation").description("Organisation"),
                fieldWithPath("organisationExecutiveOfficers[].name").description("Current Director's name of the organisation"),
                fieldWithPath("organisationStatus").description("The status of the Organisation"),
                fieldWithPath("organisationAddressSnippet").description("The Address snippet  of the Organisation")
        };
    }
}
