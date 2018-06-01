package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.builder.CompetitionClosedKeyApplicationStatisticsResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.competition.builder.CompetitionClosedKeyApplicationStatisticsResourceBuilder.newCompetitionClosedKeyApplicationStatisticsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionClosedKeyApplicationStatisticsResourceDocs {

    public static final FieldDescriptor[] competitionClosedKeyApplicationStatisticsResourceFields = {
            fieldWithPath("applicationsPerAssessor").description("The number of applications per assessor"),
            fieldWithPath("applicationsRequiringAssessors").description("The number of applications requiring more assessors"),
            fieldWithPath("assignmentCount").description("The number of assignments")
    };

    public static final CompetitionClosedKeyApplicationStatisticsResourceBuilder
            competitionClosedKeyApplicationStatisticsResourceBuilder =
            newCompetitionClosedKeyApplicationStatisticsResource()
                    .withApplicationsPerAssessor(1)
                    .withApplicationsRequiringAssessors(2)
                    .withAssignmentCount(3);
}
