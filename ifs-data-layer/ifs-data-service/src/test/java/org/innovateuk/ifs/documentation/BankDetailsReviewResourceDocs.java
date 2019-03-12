package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class BankDetailsReviewResourceDocs {
    public static final FieldDescriptor[] bankDetailsReviewResourceFields = {
            fieldWithPath("applicationId").description("Application id for the bank details review"),
            fieldWithPath("competitionId").description("Competition id for the bank details review"),
            fieldWithPath("competitionName").description("Competition name for the bank details review"),
            fieldWithPath("projectId").description("Project id for the bank details review"),
            fieldWithPath("projectName").description("Project name for the bank details review"),
            fieldWithPath("organisationId").description("Organisation id for the bank details review"),
            fieldWithPath("organisationName").description("Organisation name for the bank details review"),
    };
}
