package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class EligibilityDocs {

    public static final FieldDescriptor[] eligibilityResourceFields = {
            fieldWithPath("eligibility").description("The Eligibility for a given Project and Organisation"),
            fieldWithPath("eligibilityRagStatus").description("The Eligibility RAG Status for a given Project and Organisation"),
            fieldWithPath("eligibilityApprovalDate").description("The date on which eligibility was approved"),
            fieldWithPath("eligibilityApprovalUserFirstName").description("The first name of the user who approved the eligibility"),
            fieldWithPath("eligibilityApprovalUserLastName").description("The last name of the user who approved the eligibility")
    };
}

