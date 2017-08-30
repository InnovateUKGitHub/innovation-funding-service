package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.assessment.builder.AssessorCompetitionSummaryResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.assessment.builder.AssessorAssessmentResourceBuilder.newAssessorAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessorCompetitionSummaryResourceBuilder.newAssessorCompetitionSummaryResource;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.ACCEPTED;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.SUBMITTED;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessorCompetitionSummaryResourceDocs {

    public static final FieldDescriptor[] assessorCompetitionSummaryResourceFields = {
            fieldWithPath("assessor").description("The assessor's profile."),
            fieldWithPath("assessor.user").description("The assessor's user."),
            fieldWithPath("assessor.profile").description("The assessor's profile."),
            fieldWithPath("competitionId").description("Id of the competition that the summary is refined by."),
            fieldWithPath("competitionName").description("Name of the competition."),
            fieldWithPath("competitionStatus").description("Status of the competition."),
            fieldWithPath("assignedAssessments").description("List of the assessments that have assigned to the assessor."),
            fieldWithPath("totalApplications").description("Total number of applications for the assessor across all competitions.")
    };

    public static final AssessorCompetitionSummaryResourceBuilder assessorCompetitionSummaryResourceBuilder =
            newAssessorCompetitionSummaryResource()
                    .withCompetitionId(1L)
                    .withCompetitionName("Test Competition")
                    .withCompetitionStatus(IN_ASSESSMENT)
                    .withAssessor(
                            newAssessorProfileResource()
                                    .withUser(newUserResource().build())
                                    .withProfile(newProfileResource().build())
                                    .build()
                    )
                    .withTotalApplications(10L)
                    .withAssignedAssessments(
                            newAssessorAssessmentResource()
                                    .withApplicationId(1L, 2L)
                                    .withApplicationName("Test Application 1", "Test Application 2")
                                    .withLeadOrganisation("Lead Org 1", "Lead Org 2")
                                    .withTotalAssessors(5, 3)
                                    .withState(SUBMITTED, ACCEPTED)
                                    .build(2)
                    );
}
