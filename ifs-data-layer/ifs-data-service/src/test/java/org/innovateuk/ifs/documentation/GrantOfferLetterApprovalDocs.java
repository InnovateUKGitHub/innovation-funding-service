package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class GrantOfferLetterApprovalDocs {
    public static final FieldDescriptor[] grantOfferLetterApprovalResourceFields = {
            fieldWithPath("approvalType").description("Indicates whether the Grant Offer Letter has been accepted or rejected"),
            fieldWithPath("rejectionReason").description("The rejection reason when the Grant Offer Letter is rejected")
    };
}
