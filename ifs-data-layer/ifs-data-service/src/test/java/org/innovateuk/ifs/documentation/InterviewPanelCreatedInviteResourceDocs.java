package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class InterviewPanelCreatedInviteResourceDocs {

    public static final FieldDescriptor[] interviewPanelCreatedInviteResourceFields = {
            fieldWithPath("id").description("Id of the resource"),
            fieldWithPath("applicationId").description("Id of the application"),
            fieldWithPath("applicationName").description("Name of the application"),
            fieldWithPath("leadOrganisationName").description("Name of the Lead Organisation"),
    };
}
