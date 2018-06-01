package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.builder.CompetitionOpenKeyApplicationStatisticsResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.competition.builder.CompetitionOpenKeyApplicationStatisticsResourceBuilder.newCompetitionOpenKeyApplicationStatisticsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionOpenKeyApplicationStatisticsResourceDocs {

    public static final FieldDescriptor[] competitionOpenKeyApplicationStatisticsResourceFields = {
            fieldWithPath("applicationsPerAssessor").description("The number of applications per assessor"),
            fieldWithPath("applicationsStarted").description("The number of applications started"),
            fieldWithPath("applicationsPastHalf").description("The number of applications past 50% completion"),
            fieldWithPath("applicationsSubmitted").description("The number of applications submitted")
    };

    public static final CompetitionOpenKeyApplicationStatisticsResourceBuilder
            competitionOpenKeyApplicationStatisticsResourceBuilder =
            newCompetitionOpenKeyApplicationStatisticsResource()
                    .withApplicationsPerAssessor(1)
                    .withApplicationsStarted(2)
                    .withApplicationsPastHalf(3)
                    .withApplicationsSubmitted(4);
}
