package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompanyHouseDocs {

    public static FieldDescriptor[] organisationSearchResultFields () {
        return new FieldDescriptor[] {
                fieldWithPath("organisationSearchId").description("Id of the Organisation Search Result"),
                fieldWithPath("name").description("Name of the associated Organisation"),
                fieldWithPath("organisationAddress").description("The associated Organisation's Address"),
                fieldWithPath("organisationAddress.id").description("The associated Organisation Address Id"),
                fieldWithPath("organisationAddress.addressLine1").description("The associated Organisation Address AddressLine1"),
                fieldWithPath("organisationAddress.addressLine2").description("The associated Organisations Address AddressLine2"),
                fieldWithPath("organisationAddress.addressLine3").description("The associated Organisations Address AddressLine3"),
                fieldWithPath("organisationAddress.town").description("The associated Organisations Address town"),
                fieldWithPath("organisationAddress.county").description("The associated Organisations Address county"),
                fieldWithPath("organisationAddress.postcode").description("The associated Organisations Address postcode"),
                fieldWithPath("extraAttributes") .description("Any extra attributes of the Organisation")
        };
    }
}
