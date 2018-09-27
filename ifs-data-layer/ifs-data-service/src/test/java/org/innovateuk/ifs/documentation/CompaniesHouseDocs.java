package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompaniesHouseDocs {

    public static FieldDescriptor[] organisationSearchResultFields () {
        return new FieldDescriptor[] {
                fieldWithPath("organisationSearchId").description("Id of the Organisation Search Result"),
                fieldWithPath("name").description("Name of the associated Organisation"),
                fieldWithPath("organisationAddress").description("The associated Organisation's Address"),
                fieldWithPath("organisationAddress.id").description("The id of the Organisation's address"),
                fieldWithPath("organisationAddress.addressLine1").description("The first line of the Organisation's Address"),
                fieldWithPath("organisationAddress.addressLine2").description("The second line of the Organisation's Address"),
                fieldWithPath("organisationAddress.addressLine3").description("The third line of the Organisation's Address"),
                fieldWithPath("organisationAddress.town").description("The town of the Organisation's Address"),
                fieldWithPath("organisationAddress.county").description("The county of Organisation's Address"),
                fieldWithPath("organisationAddress.postcode").description("The postcode of the Organisation's Address"),
                fieldWithPath("extraAttributes").description("Extra attributes of the Organisation")
        };
    }
}
