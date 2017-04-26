package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.time.ZonedDateTime;

import static com.google.common.primitives.Longs.asList;
import static java.util.Collections.singleton;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionResourceDocs {
    public static final FieldDescriptor[] competitionResourceFields = {
            fieldWithPath("id").description("Id of the competitionResource"),
            fieldWithPath("name").description("name of the competition"),
            fieldWithPath("description").description("description of the competition"),
            fieldWithPath("startDate").description("date the competition opens for submissions"),
            fieldWithPath("endDate").description("date the submissions phase of the competition closes"),
            fieldWithPath("assessorAcceptsDate").description("date by which assessors should accept or reject invitations to assess applications"),
            fieldWithPath("assessorDeadlineDate").description("date by which assessors should submit their application feedback"),
            fieldWithPath("assessorBriefingDate").description("date on which assessors will be briefed on the competition"),
            fieldWithPath("fundersPanelDate").description("date on which the funders panel begins"),
            fieldWithPath("fundersPanelEndDate").description("date on which the funders panel ended"),
            fieldWithPath("assessorFeedbackDate").description("date on which applicants can expect to receive feedback from the assessments"),
            fieldWithPath("releaseFeedbackDate").description("date on which the feedback is released"),
            fieldWithPath("competitionStatus").description("the current status of the competition"),
            fieldWithPath("maxResearchRatio").description("maximum ratio of research participation"),
            fieldWithPath("academicGrantPercentage").description("grant claim percentage for the academics"),
            fieldWithPath("milestones").description("List of milestone ids"),
            fieldWithPath("competitionType").description("the competition type this competition belongs to"),
            fieldWithPath("competitionTypeName").description("the name of the competition type this competition belongs to"),
            fieldWithPath("executive").description("the user id of the competition executive"),
            fieldWithPath("executiveName").description("the name of the competition executive"),
            fieldWithPath("leadTechnologist").description("the user id of the competition leadTechnologist"),
            fieldWithPath("leadTechnologistName").description("the name of the competition leadTechnologist"),
            fieldWithPath("innovationSector").description("the Innovation sector this competition belongs to"),
            fieldWithPath("innovationSectorName").description("the Innovation sector name this competition belongs to"),
            fieldWithPath("innovationAreas").description("the Innovation areas this competition belongs to"),
            fieldWithPath("innovationAreaNames").description("the names of the Innovation areas this competition belongs to"),
            fieldWithPath("pafCode").description("the paf code entered during competition setup"),
            fieldWithPath("budgetCode").description("the budget code entered during competition setup"),
            fieldWithPath("code").description("the unique competition code entered during competition setup"),
            fieldWithPath("resubmission").description("indicates if the competition has the ability to do a resubmission"),
            fieldWithPath("multiStream").description("indicates if the competition has multiple streams"),
            fieldWithPath("streamName").description("the name of the stream"),
            fieldWithPath("collaborationLevel").description("collaboration level (single, collaborative...)"),
            fieldWithPath("leadApplicantTypes").description("permitted organisation types of lead applicant (business, research...)"),
            fieldWithPath("researchCategories").description("the research categories entered during competition setup"),
            fieldWithPath("sectionSetupStatus").description("the completion status of competition setup sections"),
            fieldWithPath("activityCode").description("the activity code entered during competition setup"),
            fieldWithPath("funders").description("the funders for this competition"),
            fieldWithPath("useResubmissionQuestion").description("should applications include the default resubmission question"),
            fieldWithPath("assessorCount").description("How many assessors are required to assess each application"),
            fieldWithPath("assessorPay").description("How much will assessors be paid per application they assess"),
            fieldWithPath("setupComplete").description("Has the setup been completed and will move to open once past the open date"),
            fieldWithPath("nonIfs").description("Is this competition a non-ifs competition (not managed via IFS)"),
            fieldWithPath("nonIfsUrl").description("The URL to apply to the competition if it is a non-ifs competition")
    };

    public static final CompetitionResourceBuilder competitionResourceBuilder = newCompetitionResource()
            .withId(1L)
            .withName("competition name")
            .withDescription("competition description")
            .withStartDate(ZonedDateTime.now())
            .withEndDate(ZonedDateTime.now().plusDays(30))
            .withAssessorAcceptsDate(ZonedDateTime.now().plusDays(35))
            .withAssessorDeadlineDate(ZonedDateTime.now().plusDays(40))
            .withFundersPanelDate(ZonedDateTime.now().plusDays(42))
            .withFundersPanelEndDate(ZonedDateTime.now().plusDays(44))
            .withAssessorFeedbackDate(ZonedDateTime.now().plusDays(56))
            .withReleaseFeedbackDate(ZonedDateTime.now().plusDays(62))
            .withMaxResearchRatio(20)
            .withAcademicGrantClaimPercentage(100)
            .withCompetitionCode("COMP-1")
            .withCompetitionType(1L)
            .withExecutive(1L)
            .withLeadTechnologist(1L)
            .withLeadTechnologistName("Competition Technologist")
            .withInnovationAreas(singleton(1L))
            .withInnovationAreaNames(singleton("Tech"))
            .withInnovationSector(2L)
            .withInnovationSectorName("IT")
            .withLeadApplicantType(asList(1L, 2L))
            .withPafCode("PAF-123")
            .withBudgetCode("BUDGET-456")
            .withActivityCode("Activity-Code")
            .withNonIfs(true)
            .withNonIfsUrl("https://google.co.uk")
            .withMilestones(asList(1L, 2L, 3L));
}
