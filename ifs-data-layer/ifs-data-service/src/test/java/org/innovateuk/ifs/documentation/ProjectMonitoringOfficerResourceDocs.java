package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ProjectMonitoringOfficerResourceDocs extends PageResourceDocs {
    public static final FieldDescriptor[] projectMonitoringOfficerResourceFields = {
            fieldWithPath("userId").description("The user id of the monitoring officer."),
            fieldWithPath("firstName").description("The first name of the monitoring officer."),
            fieldWithPath("lastName").description("The last name of the monitoring officer."),
            fieldWithPath("fullName").description("The full name of the monitoring officer.")
    };

    public static final FieldDescriptor[] monitoringOfficerUnassignedProjectResourceFields = {
            fieldWithPath("projectId").description("The ID of the project."),
            fieldWithPath("applicationId").description("The ID of the project's application."),
            fieldWithPath("projectName").description("The name of the project.")
    };

    public static final FieldDescriptor[] monitoringOfficerAssignedProjectResourceFields = {
            fieldWithPath("projectId").description("The ID of the project."),
            fieldWithPath("applicationId").description("The ID of the project's application."),
            fieldWithPath("competitionId").description("The ID of the project's competition."),
            fieldWithPath("projectName").description("The name of the project."),
            fieldWithPath("leadOrganisationName").description("The lead organisation for the project.")
    };
}