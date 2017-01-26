package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ViabilityDocs {

    public static final FieldDescriptor[] viabilityResourceFields = {
            fieldWithPath("viability").description("The Viability for a given Project and Organisation"),
            fieldWithPath("viabilityRagStatus").description("The Viability RAG Status for a given Project and Organisation"),
            fieldWithPath("viabilityApprovalDate").description("The date on which viability was approved"),
            fieldWithPath("viabilityApprovalUserFirstName").description("The first name of the user who approved the viability"),
            fieldWithPath("viabilityApprovalUserLastName").description("The last name of the user who approved the viability")
    };
}

