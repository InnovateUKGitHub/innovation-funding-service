package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.project.builder.ProjectResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.time.LocalDate;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.name;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ProjectDocs {
    public static final FieldDescriptor[] projectResourceFields = {
            fieldWithPath("id").description("Id of the project"),
            fieldWithPath("application").description("Application that the project was created from"),
            fieldWithPath("competition").description("Competition that the project was created from"),
            fieldWithPath("competitionName").description("Name of competition that the project was created from"),
            fieldWithPath("targetStartDate").description("Expected target start date for the project"),
            fieldWithPath("address").description("Address where the project is expected to be executed from"),
            fieldWithPath("durationInMonths").description("Duration that the project is expected to last"),
            fieldWithPath("name").description("The Project's name"),
            fieldWithPath("projectUsers").description("The ids of users with Roles on the Project"),
            fieldWithPath("projectMonitoringOfficer").description("The id of the project's ProjectMonitoringOfficer"),
            fieldWithPath("monitoringOfficerUser").description("The user id of the project's ProjectMonitoringOfficer"),
            fieldWithPath("financeReviewer").description("The id of the project's FinanceReviewer"),
            fieldWithPath("documentsSubmittedDate").description("Date that partner documents were submitted by the Project Manager. Null means the details have not yet been submitted"),
            fieldWithPath("offerSubmittedDate").description("Date that grant offer letter documents were submitted by the Lead partner or Project Manager. Null means the details have not yet been submitted"),
            fieldWithPath("signedGrantOfferLetter").description("Id of the File Entry that contains the Signed Grant Offer Letter"),
            fieldWithPath("additionalContractFile").description("Id of the File Entry that contains the additional contract file"),
            fieldWithPath("grantOfferLetter").description("Id of the File Entry that contains the generated Grant Offer Letter"),
            fieldWithPath("otherDocumentsApproved").description("Flag which indicates if Other Documents - Collaboration agreement and Exploitation plan, are approved or not"),
            fieldWithPath("projectDocuments").description("List of project documents for this project"),
            fieldWithPath("grantOfferLetterRejectionReason").description("Rejection reason when the Grant Offer Letter has been rejected"),
            fieldWithPath("spendProfileSubmittedDate").description("Flag which indicates if Spend Profile has been review and submitted by the Project Manager."),
            fieldWithPath("projectState").description("The current state of the project in its workflow.")
    };

    public static final FieldDescriptor[] projectStatusResourceFields = {
            fieldWithPath("projectTitle").description("Title of the project"),
            fieldWithPath("projectNumber").description("Number of the project"),
            fieldWithPath("formattedProjectNumber").description("Number of project, but formatted"),
            fieldWithPath("applicationNumber").description("Number of the application"),
            fieldWithPath("formattedApplicationNumber").description("Number of the application, but formatted"),
            fieldWithPath("numberOfPartners").description("Number of partners"),
            fieldWithPath("projectLeadOrganisationName").description("Name of the project lead organisation"),
            fieldWithPath("projectDetailsStatus").description("Status of the project details"),
            fieldWithPath("projectTeamStatus").description("Status of the project team section"),
            fieldWithPath("bankDetailsStatus").description("Status of the bank details"),
            fieldWithPath("financeChecksStatus").description("Status of the finance checks"),
            fieldWithPath("spendProfileStatus").description("Status of the spend profile"),
            fieldWithPath("monitoringOfficerStatus").description("Status of the monitoring officer"),
            fieldWithPath("documentsStatus").description("Status of the project documents"),
            fieldWithPath("grantOfferLetterStatus").description("Status of the Grant Offer Letter status"),
            fieldWithPath("projectSetupCompleteStatus").description("Status of the project setup complete section"),
            fieldWithPath("grantOfferLetterSent").description("Flag to indicate if the Grant Offer Letter notification has been sent"),
            fieldWithPath("projectState").description("State of the project")
    };

    public static final FieldDescriptor[] grantOfferLetterStateResourceFields = {
            fieldWithPath("state").description("The current workflow state of the grant offer letter"),
            fieldWithPath("lastEvent").description("The last workflow event to take place on the grant offer letter"),
    };

    @SuppressWarnings("unchecked")
    public static final ProjectResourceBuilder projectResourceBuilder = newProjectResource()
            .withId(1L)
            .with(name("Sample Project"))
            .withTargetStartDate(LocalDate.now())
            .withAddress(new AddressResource())
            .withDuration(1L)
            .withProjectUsers(asList(12L, 13L, 14L));
}
