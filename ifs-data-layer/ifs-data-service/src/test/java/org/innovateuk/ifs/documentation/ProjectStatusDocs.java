package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ProjectStatusDocs {

    public static final FieldDescriptor[] projectStatusResourceFields = {
            fieldWithPath("projectTitle").description("Project title of the project status"),
            fieldWithPath("projectNumber").description("Project number of the project status"),
            fieldWithPath("formattedProjectNumber").description("Formatter project number of the project status"),
            fieldWithPath("applicationNumber").description("Application number of the project status"),
            fieldWithPath("formattedApplicationNumber").description("Formatted application number of the project status"),
            fieldWithPath("numberOfPartners").description("Number of partners of the project status"),
            fieldWithPath("projectLeadOrganisationName").description("Project lead organisation name of the project status"),
            fieldWithPath("projectDetailsStatus").description("Project details status of the project status"),
            fieldWithPath("projectTeamStatus").description("Project team status of the project status"),
            fieldWithPath("bankDetailsStatus").description("Bank details status of the project status"),
            fieldWithPath("financeChecksStatus").description("Finance checks status of the project status"),
            fieldWithPath("spendProfileStatus").description("Spend profile status of the project status"),
            fieldWithPath("monitoringOfficerStatus").description("Monitoring officer status of the project status"),
            fieldWithPath("documentsStatus").description("Other documents status of the project status"),
            fieldWithPath("grantOfferLetterStatus").description("Grant offer letter status of the project status"),
            fieldWithPath("roleSpecificGrantOfferLetterState").description("Role specific grant offer letter state of the project status"),
            fieldWithPath("grantOfferLetterSent").description("Grant offer letter sent of the project status"),
            fieldWithPath("projectState").description("Project state"),
    };
}