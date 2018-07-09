package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ProjectPartnerStatusResourceDocs {
    public static final FieldDescriptor[] projectPartnerStatusResourceFields = {
            fieldWithPath("organisationId").description("Organisation Id of the project partner status"),
            fieldWithPath("name").description("Name of the project partner status"),
            fieldWithPath("organisationType").description("Organisation type of the project partner status"),
            fieldWithPath("projectDetailsStatus").description("Project detail status of the project partner status"),
            fieldWithPath("bankDetailsStatus").description("Bank detail status of the project partner status"),
            fieldWithPath("financeChecksStatus").description("Finance check status of the project partner status"),
            fieldWithPath("spendProfileStatus").description("Spend profile status of the project partner status"),
            fieldWithPath("financeContactStatus").description("Finance contact status of the project partner status"),
            fieldWithPath("partnerProjectLocationStatus").description("Partner project location status of the project partner status"),
            fieldWithPath("companiesHouseStatus").description("Companies house status of the project partner status"),
            fieldWithPath("monitoringOfficerStatus").description("Monitoring officer status of the project partner status"),
            fieldWithPath("otherDocumentsStatus").description("Other documents status of the project partner status"),
            fieldWithPath("grantOfferLetterStatus").description("Grant offer letter status of the project partner status"),
            fieldWithPath("lead").description("Lead of the project partner status"),
            fieldWithPath("grantOfferLetterSent").description("Grant offer letter sent of the project partner status"),
    };
}
