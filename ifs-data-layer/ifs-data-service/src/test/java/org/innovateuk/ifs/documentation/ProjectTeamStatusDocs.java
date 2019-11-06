package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ProjectTeamStatusDocs {
    public static final FieldDescriptor[] projectTeamStatusResourceFields = {
            fieldWithPath("partnerStatuses").description("Project status for each partners in the project"),
            fieldWithPath("partnerStatuses[].pendingPartner").description("Is the partner pending completion of initial details"),
            fieldWithPath("projectState").description("State of the project"),
            fieldWithPath("projectManagerAssigned").description("Whether a project manager has been assigned for this project")
    };
}
