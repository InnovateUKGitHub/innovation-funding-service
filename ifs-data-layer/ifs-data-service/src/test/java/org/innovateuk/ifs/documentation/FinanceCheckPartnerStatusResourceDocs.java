package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class FinanceCheckPartnerStatusResourceDocs {

    public static final FieldDescriptor[] financeCheckPartnerStatusResourceFields = {
            fieldWithPath("id").description("Id of the finance check partner status"),
            fieldWithPath("name").description("Name of the finance check partner status"),
            fieldWithPath("viability").description("Viability of the finance check partner status"),
            fieldWithPath("viabilityRagStatus").description("Viability rag status of the finance check partner status"),
            fieldWithPath("eligibility").description("Eligibility of the finance check partner status"),
            fieldWithPath("eligibilityRagStatus").description("Eligibility rag status of the finance check partner status"),
            fieldWithPath("awaitingResponse").description("Awaiting response of the finance check partner status"),
            fieldWithPath("financeContactProvided").description("Finance contact provided of the finance check partner status"),
            fieldWithPath("lead").description("Lead of the finance check partner status"),
    };
}
