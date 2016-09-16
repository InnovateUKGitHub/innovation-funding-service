package com.worth.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ProjectTeamStatusDocs {
    public static final FieldDescriptor[] projectTeamStatusResourceFields = {
            fieldWithPath("partnerStatuses").description("Project status for each partners in the project")
    };
}
