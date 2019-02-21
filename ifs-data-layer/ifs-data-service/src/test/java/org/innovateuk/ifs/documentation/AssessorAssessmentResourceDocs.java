package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessorAssessmentResourceDocs {
    public static final FieldDescriptor[] assessorAssessmentResourceFields = {
            fieldWithPath("applicationId").description("Application id of the assessor assessment"),
            fieldWithPath("applicationName").description("Application name of the assessor assessment"),
            fieldWithPath("leadOrganisation").description("Lead organisation of the assessor assessment"),
            fieldWithPath("totalAssessors").description("Total assessors of the assessor assessment"),
            fieldWithPath("state").description("State of the assessor assessment"),
            fieldWithPath("rejectReason").description("Reject reason of the assessor assessment"),
            fieldWithPath("rejectComment").description("Reject comment of the assessor assessment"),
            fieldWithPath("assessmentId").description("Assessment id of the assessor assessment"),
            fieldWithPath("assigned").description("Assigned of the assessor assessment"),
            fieldWithPath("rejected").description("Rejected of the assessor assessment"),
            fieldWithPath("withdrawn").description("Wthdrawn of the assessor assessment"),
    };
}
