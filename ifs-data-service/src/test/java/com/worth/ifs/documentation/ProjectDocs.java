package com.worth.ifs.documentation;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.project.builder.ProjectResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.time.LocalDate;

import static com.worth.ifs.BaseBuilderAmendFunctions.name;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static java.util.Arrays.asList;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ProjectDocs {
    public static final FieldDescriptor[] projectResourceFields = {
            fieldWithPath("id").description("Id of the project"),
            fieldWithPath("application").description("Application that the project was created from"),
            fieldWithPath("targetStartDate").description("Expected target start date for the project"),
            fieldWithPath("address").description("Address where the project is expected to be executed from"),
            fieldWithPath("durationInMonths").description("Duration that the project is expected to last"),
            fieldWithPath("name").description("The Project's name"),
            fieldWithPath("projectUsers").description("The ids of users with Roles on the Project"),
            fieldWithPath("documentsSubmittedDate").description("Date that partner documents were submitted by the project manager. Null means the details have not yet been submitted"),
            fieldWithPath("offerSubmittedDate").description("Date that grant offer letter documents were submitted by the Lead partner or project manager. Null means the details have not yet been submitted"),
            fieldWithPath("collaborationAgreement").description("Id of the File Entry that contains the Collaboration Agreement of the partner organisations"),
            fieldWithPath("exploitationPlan").description("Id of the File Entry that contains the Exploitation Plan of the partner organisations"),
            fieldWithPath("signedGrantOfferLetter").description("Id of the File Entry that contains the Signed Grant Offer Letter"),
            fieldWithPath("additionalContractFile").description("Id of the File Entry that contains tadditional contract file"),
            fieldWithPath("grantOfferLetter").description("Id of the File Entry that contains the generated Grant Offer Letter"),
            fieldWithPath("offerRejected").description("Flag to indicate if grant offer has been rejected"),
            fieldWithPath("otherDocumentsApproved").description("Flag which indicates if Other Documents - Collaboration agreement and Exploitation plan, are approved or not"),
            fieldWithPath("spendProfileSubmittedDate").description("Flag which indicates if Spend Profile has been review and submitted by the project manager.")


    };

    public static final FieldDescriptor[] projectUserResourceFields = {
            fieldWithPath("id").description("Id of the Project User record"),
            fieldWithPath("user").description("Id of the User"),
            fieldWithPath("userName").description("Full name of the User"),
            fieldWithPath("project").description("Id of the Project"),
            fieldWithPath("role").description("Id of the Role"),
            fieldWithPath("roleName").description("Name of the Role"),
            fieldWithPath("organisation").description("Id of the Organisation")
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
