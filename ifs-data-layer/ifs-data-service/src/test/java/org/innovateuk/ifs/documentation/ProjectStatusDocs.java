package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ProjectStatusDocs {
    public static final FieldDescriptor[] competitionProjectsStatusResourceFields = {
            fieldWithPath("competitionNumber").description("Formatted competition number"),
            fieldWithPath("competitionName").description("Competition name"),
            fieldWithPath("projectDocuments").description("Whether the competition is using the new configurable project documents or not"),
            fieldWithPath("projectStatusResources").description("Project status for each funded project in the requested competition")
    };
}
