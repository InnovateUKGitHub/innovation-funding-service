package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.builder.CompetitionOpenKeyStatisticsResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.competition.builder.CompetitionOpenKeyStatisticsResourceBuilder.newCompetitionOpenKeyStatisticsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionOpenKeyStatisticsResourceDocs {
    public static final FieldDescriptor[] competitionOpenKeyStatisticsResourceFields = {
            fieldWithPath("assessorsInvited").description("The number of assessors invited"),
            fieldWithPath("assessorsAccepted").description("The number of assessors who accepted the invite"),
            fieldWithPath("applicationsPerAssessor").description("The number of applications per assessor"),
            fieldWithPath("applicationsStarted").description("The number of applications started"),
            fieldWithPath("applicationsPastHalf").description("The number of applications past 50% completion"),
            fieldWithPath("applicationsSubmitted").description("The number of applications submitted")
    };

    public static final CompetitionOpenKeyStatisticsResourceBuilder competitionOpenKeyStatisticsResourceBuilder =
            newCompetitionOpenKeyStatisticsResource()
                    .withAssessorsAccepted(1)
                    .withAssessorsInvited(2)
                    .withApplicationsPerAssessor(3)
                    .withApplicationsStarted(4)
                    .withApplicationsPastHalf(5)
                    .withApplicationsSubmitted(6);
}
