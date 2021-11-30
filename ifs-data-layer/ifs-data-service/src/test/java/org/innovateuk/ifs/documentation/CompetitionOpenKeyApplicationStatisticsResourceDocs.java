package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.builder.CompetitionOpenKeyApplicationStatisticsResourceBuilder;

import static org.innovateuk.ifs.competition.builder.CompetitionOpenKeyApplicationStatisticsResourceBuilder.newCompetitionOpenKeyApplicationStatisticsResource;

public class CompetitionOpenKeyApplicationStatisticsResourceDocs {


    public static final CompetitionOpenKeyApplicationStatisticsResourceBuilder
            competitionOpenKeyApplicationStatisticsResourceBuilder =
            newCompetitionOpenKeyApplicationStatisticsResource()
                    .withApplicationsPerAssessor(1)
                    .withApplicationsStarted(2)
                    .withApplicationsPastHalf(3)
                    .withApplicationsSubmitted(4);
}
