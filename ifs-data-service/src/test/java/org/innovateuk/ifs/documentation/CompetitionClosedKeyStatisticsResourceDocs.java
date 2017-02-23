package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.builder.CompetitionClosedKeyStatisticsResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.competition.builder.CompetitionClosedKeyStatisticsResourceBuilder.newCompetitionClosedKeyStatisticsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionClosedKeyStatisticsResourceDocs {
    public static final FieldDescriptor[] competitionClosedKeyStatisticsResourceFields = {
            fieldWithPath("assessorsInvited").description("The number of assessors invited"),
            fieldWithPath("assessorsAccepted").description("The number of assessors who accepted the invite"),
            fieldWithPath("applicationsPerAssessor").description("The number of applications per assessor"),
            fieldWithPath("applicationsRequiringAssessors").description("The number of applications requiring more assessors"),
            fieldWithPath("assessorsWithoutApplications").description("The number of assessors without any applications"),
            fieldWithPath("assignmentCount").description("The number of assignments")
    };

    public static final CompetitionClosedKeyStatisticsResourceBuilder competitionClosedKeyStatisticsResourceBuilder =
            newCompetitionClosedKeyStatisticsResource()
                    .withAssessorsAccepted(1)
                    .withAssessorsInvited(2)
                    .withApplicationsPerAssessor(3)
                    .withApplicationsRequiringAssessors(4)
                    .withAssessorsWithoutApplications(5)
                    .withAssignmentCount(6);
}
