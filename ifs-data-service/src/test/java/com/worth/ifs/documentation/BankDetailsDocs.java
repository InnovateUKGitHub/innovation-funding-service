package com.worth.ifs.documentation;

import com.worth.ifs.project.bankdetails.builder.BankDetailsResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static com.worth.ifs.project.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static com.worth.ifs.organisation.builder.OrganisationAddressResourceBuilder.newOrganisationAddressResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class BankDetailsDocs {
    public static final FieldDescriptor[] bankDetailsResourceFields = {
            fieldWithPath("id").description("Id of the bankDetails record"),
            fieldWithPath("project").description("Project that the bank details belong to"),
            fieldWithPath("organisation").description("Organisation to which these bank details belong"),
            fieldWithPath("sortCode").description("Sort code for the bank, identifying a specific branch"),
            fieldWithPath("accountNumber").description("Bank account number"),
            fieldWithPath("organisationAddress").description("Banking address used by organisation"),
            fieldWithPath("organisationTypeName").description("The type of organisation"),
            fieldWithPath("companyName").description("The company name"),
            fieldWithPath("registrationNumber").description("The registration number"),
            fieldWithPath("companyNameScore").description("Score returned by experian SIL API for company name"),
            fieldWithPath("registrationNumberMatched").description("Score returned by experian SIL API for company's registration number"),
            fieldWithPath("addressScore").description("Score returned by experian SIL API for company's banking address"),
            fieldWithPath("manualApproval").description("Flag to verify bank details are valid manually by IFS finance staff"),
            fieldWithPath("verified").description("Flag to signify that experian validation has been completed sucessfully")
    };

    @SuppressWarnings("unchecked")
    public static final BankDetailsResourceBuilder bankDetailsResourceBuilder = newBankDetailsResource()
            .withId(1L)
            .withProject(1L)
            .withOrganisation(1L)
            .withSortCode("123456")
            .withAccountNumber("12345678")
            .withOrganiationAddress(newOrganisationAddressResource().build());

    public static final FieldDescriptor[] projectBankDetailsStatusSummaryFields = {
            fieldWithPath("competitionId").description("Competition Id"),
            fieldWithPath("competitionName").description("Competition name"),
            fieldWithPath("projectId").description("Project id to which the "),
            fieldWithPath("bankDetailsStatusResources").description("Bank details status")
    };
}
