package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class InterviewAssignmentApplicationResourceDocs {

    public static final FieldDescriptor[] interviewAssignmentAssignedResourceFields = {
            fieldWithPath("id").description("Id of the resource"),
            fieldWithPath("applicationId").description("Id of the application"),
            fieldWithPath("applicationName").description("Name of the application"),
            fieldWithPath("leadOrganisationName").description("Name of the Lead Organisation"),
            fieldWithPath("status").description("Status of application response to feedback"),
    };
}
