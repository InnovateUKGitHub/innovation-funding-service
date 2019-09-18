package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.competition.resource.CollaborationLevel.SINGLE_OR_COLLABORATIVE;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ApplicationDocs {
    public static final FieldDescriptor[] applicationResourceFields = {
            fieldWithPath("id").description("Id of the application").optional(),
            fieldWithPath("name").description("Name of the application").optional(),
            fieldWithPath("startDate").description("Estimated timescales: project start date").optional(),
            fieldWithPath("submittedDate").description("The date the applicant has submitted this application.").optional(),
            fieldWithPath("durationInMonths").description("Estimated timescales: project duration in months").optional(),
            fieldWithPath("applicationState").description("ApplicationState").optional(),
            fieldWithPath("stateAidAgreed").description("Flag indicating if the user has accepted that they are eligible for state aid").optional(),
            fieldWithPath("competition").description("Competition Id").optional(),
            fieldWithPath("competitionName").description("Competition Name").optional(),
            fieldWithPath("competitionStatus").description("Competition Status").optional(),
            fieldWithPath("completion").description("percentage of completion of the application").optional(),
            fieldWithPath("resubmission").description("indicator that this application is a resubmission").optional(),
            fieldWithPath("previousApplicationNumber").description("the application number of the previous submission").optional(),
            fieldWithPath("previousApplicationTitle").description("the application title of the previous submission").optional(),
            fieldWithPath("researchCategory").description("Research category").optional(),
            fieldWithPath("innovationArea").description("applicable Innovation Area").optional(),
            fieldWithPath("noInnovationAreaApplicable").description("Flag indicating no Innovation Area is applicable").optional(),
            fieldWithPath("ineligibleOutcome").description("Outcome describing why the application has been marked as ineligible").optional(),
            fieldWithPath("leadOrganisationId").description("the id of the lead organisation").optional(),
            fieldWithPath("inAssessmentReviewPanel").description("Whether the requested application has been chosen for assessment review panel").optional(),
            fieldWithPath("collaborationLevel").description("Collaboration level of the Competition (single, collaborative...)").optional(),
            fieldWithPath("collaborativeProject").description("Flag indicating if the project is collaborative").optional(),
            fieldWithPath("competitionReferralSource").description("Enum indicating how the applicant was referred to the competition").optional(),
            fieldWithPath("companyAge").description("Enum indicating age of company applying").optional(),
            fieldWithPath("companyPrimaryFocus").description("Enum indicating the sector of business of the company applying").optional()
    };

    public static final FieldDescriptor[] previousApplicationResourceFields = {
            fieldWithPath("id").description("Id of the application").optional(),
            fieldWithPath("name").description("Name of the application").optional(),
            fieldWithPath("leadOrganisationName").description("the name of the lead organisation").optional(),
            fieldWithPath("applicationState").description("ApplicationState").optional(),
            fieldWithPath("competition").description("Competition Id").optional(),
    };

    public static final ApplicationResourceBuilder applicationResourceBuilder = newApplicationResource()
            .withId(1L)
            .withName("application name")
            .withStartDate(LocalDate.now())
            .withSubmittedDate(ZonedDateTime.now())
            .withDurationInMonths(1L)
            .withApplicationState(ApplicationState.OPENED)
            .withCompetition(1L)
            .withCompetitionName("competition name")
            .withCompetitionStatus(CompetitionStatus.PROJECT_SETUP)
            .withCompletion(new BigDecimal(30L))
            .withResearchCategory(new ResearchCategoryResource())
            .withInnovationArea(new InnovationAreaResource())
            .withLeadOrganisationId(1L)
            .withNoInnovationAreaApplicable(false)
            .withCollaborationLevel(SINGLE_OR_COLLABORATIVE);
}
